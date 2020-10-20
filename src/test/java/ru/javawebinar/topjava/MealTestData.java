package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

public class MealTestData {
    public static final int MEAL_ID = ADMIN_ID + 1;

    public static final Meal meal1forUser = new Meal(MEAL_ID, LocalDateTime.parse("2020-10-17T09:12:00"), "Завтрак", 450);
    public static final Meal meal2forUser = new Meal(MEAL_ID + 1, LocalDateTime.parse("2020-10-17T12:15:00"), "Обед", 800);
    public static final Meal meal3forUser = new Meal(MEAL_ID + 2, LocalDateTime.parse("2020-10-17T20:24:00"), "Ужин", 250);

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
