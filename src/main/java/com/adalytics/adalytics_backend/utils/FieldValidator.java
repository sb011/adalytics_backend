package com.adalytics.adalytics_backend.utils;

public class FieldValidator {
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:[A-Z0-9-]+\\.)+[A-Z]{2,6}$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }

        String uppercaseRegex = "[A-Z]";
        String lowercaseRegex = "[a-z]";
        String specialCharRegex = "[^\\w\\s]";

        if (password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.matches(uppercaseRegex);
        boolean hasLowercase = password.matches(lowercaseRegex);
        boolean hasSpecialChar = password.matches(specialCharRegex);

        return hasUppercase && hasLowercase && hasSpecialChar;
    }
}
