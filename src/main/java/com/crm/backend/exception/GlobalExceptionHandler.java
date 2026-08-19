package com.crm.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Pattern SECONDS_PATTERN = Pattern.compile("in (\\d+) seconds");

    // 1. Handles bad credentials & lockout time parsing for login
    @ExceptionHandler(BadCredentialsExceptionWithAttempts.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsExceptionWithAttempts ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("attemptsRemaining", ex.getAttemptsRemaining());

        Matcher matcher = SECONDS_PATTERN.matcher(ex.getMessage());
        if (matcher.find()) {
            body.put("secondsLeft", Integer.parseInt(matcher.group(1)));
        } else {
            body.put("secondsLeft", 0);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 2. Handles DTO field validation errors (@Valid) from main
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 3. General runtime exception handler returning standard 'message' key for React
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}