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
    Token_Expired(14),
    Email_Not_Verified(15),

    User_Not_Found(21),
    Password_Not_Matching(22),

    Platform_Token_Invalid(31),
    Platform_Invalid(32),
    Platform_Error(33),

    Connector_Not_Found(34),
    Invalid_Request_Body(35),
    Connector_Already_Present(36),

    Client_Not_Responding(41),
    Method_Not_Allowed(42),

    Organization_Name_Invalid(51),

    Failed_To_Send_Email(61),

    Json_Processing_error(71);

    private final int errorCode;
    ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }
}
