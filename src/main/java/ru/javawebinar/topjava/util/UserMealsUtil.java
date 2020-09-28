package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;
import java.util.stream.Collector;
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

        // call optional2 methods

        List<UserMealWithExcess> mealsTo2 = filteredByCyclesOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo2.forEach(System.out::println);

        System.out.println(filteredByStreamsOptional(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    /**
     * Return filtered list of UserMealWithExcess from UserMeal list
     * implementation with cycles
     * @param meals UserMeal list
     * @param startTime including
     * @param endTime excluding
     * @param caloriesPerDay (excess in UserMealWithExcess == true, if caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDates = new HashMap<>();
        meals.forEach(meal -> caloriesPerDates.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));

        List<UserMealWithExcess> results = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                results.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesPerDates.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }
        return results;
    }

    /**
     * Return filtered list of UserMealWithExcess from UserMeal list
     * implementation with Streams
     * @param meals UserMeal list
     * @param startTime including
     * @param endTime excluding
     * @param caloriesPerDay (excess in UserMealWithExcess == true, if caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDates = meals.stream()
                .collect(Collectors.groupingBy(x -> x.getDateTime().toLocalDate(), Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        caloriesPerDates.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    /**
     * Return filtered list of UserMealWithExcess from UserMeal list
     * implementation with cycles (for HW0 Optional 2)
     * @param meals UserMeal list
     * @param startTime including
     * @param endTime excluding
     * @param caloriesPerDay (excess in UserMealWithExcess == true, if caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByCyclesOptional(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        Map<LocalDate, Integer> caloriesPerDates = new HashMap<>();
        Map<LocalDate, AtomicBoolean> excessPerDates = new HashMap<>();

        for (UserMeal meal : meals) {
            LocalDate date = meal.getDateTime().toLocalDate();
            caloriesPerDates.merge(date, meal.getCalories(), Integer::sum);

            AtomicBoolean excess = excessPerDates.compute(date, (k, v) -> {
                if (v == null)
                    v = new AtomicBoolean();
                v.set(caloriesPerDates.get(date) > caloriesPerDay);
                return v;
            });

            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
            }
        }
        return result;
    }

    /**
     * Return filtered list of UserMealWithExcess from UserMeal list
     * implementation with Streams (for HW0 Optional 2)
     * @param meals UserMeal list
     * @param startTime including
     * @param endTime excluding
     * @param caloriesPerDay (excess in UserMealWithExcess == true, if caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByStreamsOptional(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>> mealsCollector =
                new Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>>() {
                    private Map<LocalDate, Integer> caloriesPerDates = new HashMap<>();
                    private Map<LocalDate, AtomicBoolean> excessPerDates = new HashMap<>();

                    @Override
                    public Supplier<List<UserMealWithExcess>> supplier() {
                        return ArrayList::new;
                    }

                    @Override
                    public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
                        return (result, meal) -> {
                            LocalDate date = meal.getDateTime().toLocalDate();
                            caloriesPerDates.merge(date, meal.getCalories(), Integer::sum);

                            AtomicBoolean excess = excessPerDates.compute(date, (k, v) -> {
                                if (v == null)
                                    v = new AtomicBoolean();
                                v.set(caloriesPerDates.get(date) > caloriesPerDay);
                                return v;
                            });

                            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                                result.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
                            }
                        };
                    }

                    @Override
                    public BinaryOperator<List<UserMealWithExcess>> combiner() {
                        return (o, o2) -> {
                            o.addAll(o2);
                            return o;
                        };
                    }

                    @Override
                    public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
                        return (o) -> o;
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
                    }
                };

        return new ArrayList<>(meals.stream()
                .collect(mealsCollector));
    }
}
