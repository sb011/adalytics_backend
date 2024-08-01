package com.adalytics.adalytics_backend.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    public static <T> List<T> nullSafeList(List<T> list) {
        return (list != null ? list : new ArrayList<>());
    }

    public static String convertMillisToDate(Long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
