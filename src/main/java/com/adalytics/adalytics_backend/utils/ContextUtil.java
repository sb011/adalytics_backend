package com.adalytics.adalytics_backend.utils;

import com.adalytics.adalytics_backend.models.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextUtil {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        FieldValidator.validateUserId(user.getId());
        return user.getId();
    }

    public static String getCurrentOrgId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        FieldValidator.validateUserId(user.getOrganizationId());
        return user.getOrganizationId();
    }
}