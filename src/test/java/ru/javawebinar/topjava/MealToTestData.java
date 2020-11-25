package ru.javawebinar.topjava;

import ru.javawebinar.topjava.to.MealTo;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.util.MealsUtil.createTo;

public class MealToTestData {
    public static final TestMatcher<MealTo> MEAL_TO_MATCHER = TestMatcher.usingEquals(MealTo.class);

    public static final MealTo mealTo1 = createTo(meal1, false);
    public static final MealTo mealTo2 = createTo(meal2, false);
    public static final MealTo mealTo3 = createTo(meal3, false);
    public static final MealTo mealTo4 = createTo(meal4, true);
    public static final MealTo mealTo5 = createTo(meal5, true);
    public static final MealTo mealTo6 = createTo(meal6, true);
    public static final MealTo mealTo7 = createTo(meal7, true);

    public static final List<MealTo> mealTos = List.of(mealTo7, mealTo6, mealTo5, mealTo4, mealTo3, mealTo2, mealTo1);
}
