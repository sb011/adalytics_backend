package com.adalytics.adalytics_backend.enums;

public enum ErrorCodes {
    Internal_Server_Error(0),
    Signup_Email_Empty(1);

    private final int errorCode;
    ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
