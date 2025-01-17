package com.adalytics.adalytics_backend.exceptions;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String errorMessage;
    private final int errorCode;

    public NotFoundException(String errorMessage, int errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}