package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;

public class MealTestData {
    public static final int MEAL_ID = ADMIN_ID + 1;
    public static final int NOT_EXIST_MEAL_ID = 1;

    public static final Meal meal1forUser = new Meal(MEAL_ID, LocalDateTime.of(2020, 10, 17, 9, 12), "Завтрак", 450);
    public static final Meal meal2forUser = new Meal(MEAL_ID + 1, LocalDateTime.of(2020, 10, 17, 12, 15), "Обед", 800);
    public static final Meal meal3forUser = new Meal(MEAL_ID + 2, LocalDateTime.of(2020, 10, 17, 20, 24), "Ужин", 250);
    public static final Meal meal4forUser = new Meal(MEAL_ID + 3, LocalDateTime.of(2020, 10, 19, 14, 52), "Праздничный обед", 1860);

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2020, 10, 18, 9, 12), "Обед", 1200);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal1forUser);
        updated.setCalories(100);
        updated.setDescription("Новый завтрак");
        updated.setDateTime(LocalDateTime.of(2020, 11, 6, 9, 43));
        return updated;
    }
}
