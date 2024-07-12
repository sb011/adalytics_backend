package com.adalytics.adalytics_backend.enums;

import lombok.Getter;

@Getter
public enum MetricType {
    LINE("line");

    private final String displayName;

    MetricType(String displayName) {
        this.displayName = displayName;
    }
}
