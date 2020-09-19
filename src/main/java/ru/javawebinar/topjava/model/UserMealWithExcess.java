package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;

public class UserMealWithExcess {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    // array-wrapper for boolean (so boolean has object properties)
    private final Boolean[] excess;

    public UserMealWithExcess(LocalDateTime dateTime, String description, int calories, Boolean[] excess) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    @Override
    public String toString() {
        return "UserMealWithExcess{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + excess[0] +
                '}';
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Boolean[] getExcess() {
        return excess;
    }
}
