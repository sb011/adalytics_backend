package com.adalytics.adalytics_backend.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class CommonUtil {

    public static <T> T resolve(Supplier<T> resolver, T defaultValue) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result).get();
        } catch (Exception ex) {
            return defaultValue;
        }
    }
}
