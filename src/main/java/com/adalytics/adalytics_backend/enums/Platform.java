package com.adalytics.adalytics_backend.enums;

import lombok.Getter;

@Getter
public enum Platform {
    FACEBOOK("Facebook"),
    GOOGLE("Google");

    private final String displayName;

    Platform(String displayName) {
        this.displayName = displayName;
    }
}
