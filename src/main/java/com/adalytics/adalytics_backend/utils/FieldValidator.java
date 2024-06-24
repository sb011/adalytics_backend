package com.adalytics.adalytics_backend.utils;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.enums.Platform;
import com.adalytics.adalytics_backend.exceptions.BadRequestException;
import com.adalytics.adalytics_backend.exceptions.NotFoundException;
import org.apache.commons.lang3.EnumUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.logging.log4j.util.Strings.isBlank;

public class FieldValidator {
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        String regExp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        Pattern pattern = Pattern.compile(regExp);

        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static void validateUserId(String userId) {
        if(isBlank(userId)) {
            throw new NotFoundException("User Not Found", ErrorCodes.User_Not_Found.getErrorCode());
        }
    }

    public static void validatePlatformToken(String token) {
        if(isBlank(token)) {
            throw new BadRequestException("Platform Token is Invalid", ErrorCodes.Platform_Token_Invalid.getErrorCode());
        }
    }

    public static void validatePlatformName(String platform) {
        if(isBlank(platform) || !EnumUtils.isValidEnum(Platform.class,platform)) {
            throw new BadRequestException("Platform is Not Supported", ErrorCodes.Platform_Invalid.getErrorCode());
        }
    }

    public static void validateConnectorId(String userId) {
        if(isBlank(userId)) {
            throw new NotFoundException("Connector Not Found", ErrorCodes.Connector_Not_Found.getErrorCode());
        }
    }
}
