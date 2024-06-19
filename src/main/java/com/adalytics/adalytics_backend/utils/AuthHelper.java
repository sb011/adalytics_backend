package com.adalytics.adalytics_backend.utils;

import com.adalytics.adalytics_backend.constants.CommonConstants;

public class AuthHelper {
    public static String getUsernameFromEmail(String email) {
        if (email == null || email.isEmpty()) {
            return CommonConstants.DEFAULT_USERNAME;
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return CommonConstants.DEFAULT_USERNAME;
        }

        return email.substring(0, atIndex);
    }
}
