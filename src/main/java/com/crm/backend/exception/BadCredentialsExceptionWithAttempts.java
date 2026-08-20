package com.crm.backend.exception;

import lombok.Getter;

@Getter
public class BadCredentialsExceptionWithAttempts extends RuntimeException {
    private final int attemptsRemaining;

    public BadCredentialsExceptionWithAttempts(String message, int attemptsRemaining) {
        super(message);
        this.attemptsRemaining = attemptsRemaining;
    }
}