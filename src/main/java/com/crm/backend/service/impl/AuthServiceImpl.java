package com.crm.backend.service.impl;

import com.crm.backend.config.JwtUtil;
import com.crm.backend.dto.LoginRequest;
import com.crm.backend.dto.LoginResponse;
import com.crm.backend.entity.User;
import com.crm.backend.exception.BadCredentialsExceptionWithAttempts;
import com.crm.backend.repository.UserRepository;
import com.crm.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_SECONDS = 30;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final Map<String, Long> lockoutCache = new ConcurrentHashMap<>();

    @Override
    @Transactional(noRollbackFor = BadCredentialsExceptionWithAttempts.class)
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        String email = loginRequest.getUsername();

        // 1. Validate if 30-second lockout is still active or expired
        checkAndHandleLockout(email);

        // 2. Fetch user or throw error
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsExceptionWithAttempts("Invalid email or password.", MAX_ATTEMPTS));

        // 3. Fallback check for DB-level lock status
        verifyDatabaseLockState(user, email);

        // 4. Validate credentials (Plain text comparison)
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            handleFailedLogin(user, email);
        }

        // 5. Successful login
        return handleSuccessfulLogin(user, email);
    }

    // --- Private Helper Methods ---

    private void checkAndHandleLockout(String email) {
        if (!lockoutCache.containsKey(email)) return;

        long expiryTime = lockoutCache.get(email);
        long now = Instant.now().toEpochMilli();

        if (now < expiryTime) {
            long secondsLeft = Math.max(1, (expiryTime - now) / 1000);
            throw new BadCredentialsExceptionWithAttempts(
                    "Account is locked. Try again in " + secondsLeft + " seconds.", 0
            );
        }

        // 30 seconds passed -> Auto-unlock and grant 1 final attempt
        lockoutCache.remove(email);
        userRepository.findByEmail(email).ifPresent(user ->
                userRepository.updateAttemptsDirectly(user.getId(), MAX_ATTEMPTS - 1, false)
        );
    }

    private void verifyDatabaseLockState(User user, String email) {
        if (Boolean.TRUE.equals(user.getIsLocked())) {
            lockoutCache.put(email, Instant.now().plusSeconds(LOCKOUT_DURATION_SECONDS).toEpochMilli());
            throw new BadCredentialsExceptionWithAttempts("Account is locked due to consecutive failed attempts.", 0);
        }
    }

    private void handleFailedLogin(User user, String email) {
        int currentAttempts = user.getWrongAttempts() != null ? user.getWrongAttempts() : 0;
        int newAttempts = currentAttempts + 1;
        boolean isLocked = newAttempts >= MAX_ATTEMPTS;

        userRepository.updateAttemptsDirectly(user.getId(), newAttempts, isLocked);

        if (isLocked) {
            lockoutCache.put(email, Instant.now().plusSeconds(LOCKOUT_DURATION_SECONDS).toEpochMilli());
        }

        int remaining = Math.max(0, MAX_ATTEMPTS - newAttempts);
        throw new BadCredentialsExceptionWithAttempts("Invalid email or password.", remaining);
    }

    private LoginResponse handleSuccessfulLogin(User user, String email) {
        lockoutCache.remove(email);
        userRepository.resetAttemptsDirectly(user.getId());

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                "Login successful!",
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                MAX_ATTEMPTS
        );
    }
}