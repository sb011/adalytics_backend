package com.adalytics.adalytics_backend.enums;

public enum ErrorCodes {
    Internal_Server_Error(0),

    Signup_Email_Invalid(1),
    Signup_Password_Invalid(2),

    Login_Email_Invalid(11),
    Login_Password_Invalid(12),
    Token_Invalid(13),

    User_Not_Found(21),
    Password_Not_Matching(22);

    private final int errorCode;
    ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
