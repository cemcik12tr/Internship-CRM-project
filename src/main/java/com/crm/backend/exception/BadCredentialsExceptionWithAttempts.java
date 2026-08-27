package com.crm.backend.exception;

import lombok.Getter;

@Getter
public class BadCredentialsExceptionWithAttempts extends RuntimeException {

    private final Integer attemptsRemaining;

    public BadCredentialsExceptionWithAttempts(String message, Integer attemptsRemaining) {
        super(message);
        this.attemptsRemaining = attemptsRemaining;
    }
}