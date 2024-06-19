package com.adalytics.adalytics_backend.exceptions;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private String errorMessage;
    private int errorCode;

    public BadRequestException(String errorMessage, int errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
