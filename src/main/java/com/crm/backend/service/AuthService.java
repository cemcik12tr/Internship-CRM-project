package com.crm.backend.service;

import com.crm.backend.dto.LoginRequest;
import com.crm.backend.dto.LoginResponse;
import com.crm.backend.entity.User;
import com.crm.backend.exception.BadCredentialsExceptionWithAttempts;
import com.crm.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, Long> lockoutCache = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_SECONDS = 30;

    @Transactional(noRollbackFor = BadCredentialsExceptionWithAttempts.class)
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        String email = loginRequest.getUsername();

        // 1. Handle active lockout validation or expiration check (ACC-05)
        checkAndHandleLockout(email);

        // 2. Fetch user or throw standard error
        User user = findUserOrThrow(email);

        // 3. Handle database-level lock fallbacks
        verifyDatabaseLockState(user, email);

        // 4. Validate password credentials
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            handleFailedLogin(user, email);
        }

        // 5. Handle successful login cleanup
        return handleSuccessfulLogin(user, email);
    }

    @Transactional
    public void unlockAccount(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            userRepository.updateAttemptsDirectly(user.getId(), 4, false);
            lockoutCache.remove(email);
        });
    }

    // --- Helper Methods to Keep the Main Flow Clean ---

    private void checkAndHandleLockout(String email) {
        if (!lockoutCache.containsKey(email)) return;

        long expiryTime = lockoutCache.get(email);
        if (Instant.now().toEpochMilli() < expiryTime) {
            long secondsLeft = (expiryTime - Instant.now().toEpochMilli()) / 1000;
            throw new RuntimeException("Account is locked. Please try again in " + Math.max(1, secondsLeft) + " seconds.");
        } else {
            // ACC-05: Timer expired -> Unlock and grant 1 final attempt (attempts = 4)
            lockoutCache.remove(email);
            userRepository.findByEmail(email).ifPresent(user ->
                    userRepository.updateAttemptsDirectly(user.getId(), 4, false)
            );
        }
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));
    }

    private void verifyDatabaseLockState(User user, String email) {
        if (Boolean.TRUE.equals(user.getIsLocked())) {
            lockoutCache.put(email, Instant.now().plusSeconds(LOCKOUT_DURATION_SECONDS).toEpochMilli());
            throw new RuntimeException("Account is locked due to consecutive failed login attempts. Please try again in 30 seconds.");
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
        return new LoginResponse("Login successful!", user.getEmail(), MAX_ATTEMPTS);
    }
}