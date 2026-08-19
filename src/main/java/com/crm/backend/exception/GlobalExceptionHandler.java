package com.crm.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Pattern SECONDS_PATTERN = Pattern.compile("in (\\d+) seconds");

    @ExceptionHandler(BadCredentialsExceptionWithAttempts.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsExceptionWithAttempts ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("attemptsRemaining", ex.getAttemptsRemaining());

        // Extract remaining seconds so React doesn't have to guess or hardcode 30
        Matcher matcher = SECONDS_PATTERN.matcher(ex.getMessage());
        if (matcher.find()) {
            body.put("secondsLeft", Integer.parseInt(matcher.group(1)));
        } else {
            body.put("secondsLeft", 0);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}