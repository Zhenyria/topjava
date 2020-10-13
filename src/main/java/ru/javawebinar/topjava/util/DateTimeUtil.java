package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Implementation with overriding methods (strong type-safety)
//    public static boolean isBetweenHalfOpen(LocalDateTime currentTime, LocalDateTime startTime, LocalDateTime endTime) {
//        return isBetweenHalfOpen(currentTime, startTime, endTime);
//    }
//
//    public static boolean isBetweenHalfOpen(LocalDate currentTime, LocalDate startTime, LocalDate endTime) {
//        return isBetweenHalfOpen(currentTime, startTime, endTime);
//    }
//
//    public static boolean isBetweenHalfOpen(LocalTime currentTime, LocalTime startTime, LocalTime endTime) {
//        return isBetweenHalfOpen(currentTime, startTime, endTime);
//    }
//
//    private static <T extends Comparable<T>, E extends T> boolean isBetweenHalfOpen(E currentTime, E startTime, E endTime) {
//        return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) < 0;
//    }

    public static <T extends Comparable<T>, E extends T> boolean isBetweenHalfOpen(E currentTime, E startTime, E endTime) {
        return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) < 0;
    }

    public static <T extends Comparable<T>, E extends T> boolean isBetween(E currentTime, E startTime, E endTime) {
        return currentTime.compareTo(startTime) >= 0 && currentTime.compareTo(endTime) <= 0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}

