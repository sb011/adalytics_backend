package com.adalytics.adalytics_backend.enums;

public enum ErrorCodes {
    Internal_Server_Error(0),
    Signup_Email_Invalid(1),
    Signup_Password_Invalid(2);

    private final int errorCode;
    ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
