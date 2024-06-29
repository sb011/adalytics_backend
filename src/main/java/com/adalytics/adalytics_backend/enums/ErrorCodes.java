package com.adalytics.adalytics_backend.enums;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    Internal_Server_Error(0),

    Signup_Email_Invalid(1),
    Signup_Password_Invalid(2),

    Login_Email_Invalid(11),
    Login_Password_Invalid(12),
    Token_Invalid(13),

    User_Not_Found(21),
    Password_Not_Matching(22),

    Platform_Token_Invalid(31),
    Platform_Invalid(32),
    Connector_Not_Found(33),
    Invalid_Request_Body(34),

    Client_Not_Responding(41),
    Method_Not_Allowed(42),

    Organization_Name_Invalid(51);

    private final int errorCode;
    ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }
}
