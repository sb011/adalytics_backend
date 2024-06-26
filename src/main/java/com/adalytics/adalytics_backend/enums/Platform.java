package com.adalytics.adalytics_backend.enums;

public enum Platform {
    FACEBOOK("Facebook");

    private String displayName;

    Platform(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
