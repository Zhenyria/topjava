package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    /**
     * Возвращает отфильтрованный список UserMealWithExcess из переданного списка UserMeal
     * @param meals список UserMeal
     * @param startTime начальное время дня (включая)
     * @param endTime конечное время дня (исключая)
     * @param caloriesPerDay (excess в UserMealWithExcess == true, если caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Boolean> excessPerDatesMap = excessPerDate(meals, caloriesPerDay);
        List<UserMealWithExcess> filterResults = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            if (TimeUtil.isBetweenHalfOpen(LocalTime.from(userMeal.getDateTime()), startTime, endTime)) {
                LocalDateTime dateTime = userMeal.getDateTime();
                String description = userMeal.getDescription();
                int calories = userMeal.getCalories();
                boolean isExcess = excessPerDatesMap.get(LocalDate.from(dateTime));
                filterResults.add(new UserMealWithExcess(dateTime, description, calories, isExcess));
            }
        }
        return filterResults;
    }

    /**
     * Возвращает отфильтрованный список UserMealWithExcess из переданного списка UserMeal
     * @param meals список UserMeal
     * @param startTime начальное время дня (включая)
     * @param endTime конечное время дня (исключая)
     * @param caloriesPerDay (excess в UserMealWithExcess == true, если caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Boolean> excessPerDatesMap = excessPerDate(meals, caloriesPerDay);
        return meals.stream()
                .filter(x -> TimeUtil.isBetweenHalfOpen(LocalTime.from(x.getDateTime()), startTime, endTime))
                .map(x -> new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(), excessPerDatesMap.get(LocalDate.from(x.getDateTime()))
                ))
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод. Возвращает карту, в которой key - дата, value - превышено ли количество калорий за этот день
     */
    private static Map<LocalDate, Boolean> excessPerDate(List<UserMeal> meals, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDate = new HashMap<>();
        for (UserMeal userMeal : meals) {
            caloriesPerDate.merge(LocalDate.from(userMeal.getDateTime()), userMeal.getCalories(), Integer::sum);
        }
        Map<LocalDate, Boolean> excessPerDatesMap = new HashMap<>();
        caloriesPerDate.forEach((k, v) -> excessPerDatesMap.put(k, v > caloriesPerDay));
        return excessPerDatesMap;
    }
}
