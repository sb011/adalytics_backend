package com.adalytics.adalytics_backend.enums;

import lombok.Getter;

@Getter
public enum MetricType {
    LINE("line"),
    BAR("bar");

    private final String displayName;

    MetricType(String displayName) {
        this.displayName = displayName;
    }
}
