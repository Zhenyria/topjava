package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
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
    }

    /**
     * Возвращает отфильтрованный список UserMealWithExcess из переданного списка UserMeal
     * @param meals список UserMeal
     * @param startTime начальное время дня (включая)
     * @param endTime конечное время дня (исключая)
     * @param caloriesPerDay (excess в UserMealWithExcess == true, если caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, ExcessEntry<Boolean[], Integer>> dateExcess = new HashMap<>();
        List<UserMealWithExcess> result = new ArrayList<>();

        for (UserMeal x : meals) {
            LocalDate currentDate = LocalDate.from(x.getDateTime());
            int calories = x.getCalories();
            Boolean[] excess;
            if (dateExcess.containsKey(currentDate)) {
                ExcessEntry<Boolean[], Integer> excessEntry = dateExcess.get(currentDate);
                excessEntry.setValue(excessEntry.getValue() + calories);
                if (excessEntry.getValue() > caloriesPerDay) excessEntry.getKey()[0] = true;
                excess = excessEntry.getKey();
            }
            else {
                dateExcess.put(currentDate, new ExcessEntry<>(new Boolean[1], calories));
                excess = dateExcess.get(currentDate).getKey();
                excess[0] = calories > caloriesPerDay;
            }

            if (TimeUtil.isBetweenHalfOpen(LocalTime.from(x.getDateTime()), startTime, endTime)) {
                result.add(new UserMealWithExcess(x.getDateTime(), x.getDescription(), x.getCalories(), excess));
            }
        }

        return result;
    }

    /**
     * Возвращает отфильтрованный список UserMealWithExcess из переданного списка UserMeal
     * @param meals список UserMeal
     * @param startTime начальное время дня (включая)
     * @param endTime конечное время дня (исключая)
     * @param caloriesPerDay (excess в UserMealWithExcess == true, если caloriesPerDay < UserMeal.getCalories())
     */
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>> mealsCollector =
                new Collector<UserMeal, List<UserMealWithExcess>, List<UserMealWithExcess>>() {
                    private Map<LocalDate, Integer> dateExcess = new HashMap<>();

                    @Override
                    public Supplier<List<UserMealWithExcess>> supplier() {
                        return ArrayList::new;
                    }

                    @Override
                    public BiConsumer<List<UserMealWithExcess>, UserMeal> accumulator() {
                        return (o, o2) -> {
                            o.add(new UserMealWithExcess(o2.getDateTime(), o2.getDescription(), o2.getCalories(), new Boolean[]{false}));
                            dateExcess.merge(LocalDate.from(o2.getDateTime()), o2.getCalories(), Integer::sum);
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
                        return (o) -> {
                            o.forEach(x -> x.getExcess()[0] = (dateExcess.get(LocalDate.from(x.getDateTime())) > caloriesPerDay));
                            return o;
                        };
                    }

                    @Override
                    public Set<Characteristics> characteristics() {
                        return Collections.unmodifiableSet(EnumSet.of(Characteristics.UNORDERED));
                    }
                };

        return meals.stream()
                .collect(mealsCollector)
                .stream()
                .filter(x -> TimeUtil.isBetweenHalfOpen(LocalTime.from(x.getDateTime()), startTime, endTime))
                .collect(Collectors.toList());
    }

    private static class ExcessEntry<K, V> {
        private final K key;
        private V value;

        public ExcessEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
