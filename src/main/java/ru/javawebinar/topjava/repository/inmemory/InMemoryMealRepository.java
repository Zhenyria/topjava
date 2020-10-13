package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Comparator<Meal> comparator = (meal, meal1) -> meal1.getDateTime().compareTo(meal.getDateTime());

    {
        // test repository initialization
        MealsUtil.meals.forEach(x -> save(1, x));
        MealsUtil.secondMeals.forEach(x -> save(2, x));
        SecurityUtil.setAuthUserId(1);
    }

    @Override
    public Meal save(int userId, Meal meal) {
        Map<Integer, Meal> mealRepository = repository.computeIfAbsent(userId, x -> new ConcurrentHashMap<>());
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            mealRepository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return mealRepository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        Map<Integer, Meal> mealRepository = repository.get(userId);
        if (mealRepository == null) {
            return false;
        }
        return mealRepository.remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        Map<Integer, Meal> mealRepository = repository.get(userId);
        if (mealRepository == null) {
            return null;
        }
        return mealRepository.get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return getFilteredOrderedList(userId, m -> true);
    }

    @Override
    public Collection<Meal> getFilterResults(int userId, LocalDate startDate, LocalDate endDate) {
        return getFilteredOrderedList(userId, m -> DateTimeUtil.isBetween(m.getDate(), startDate, endDate));
    }

    /**
     * Util method return filtered Meal List for current user (by means of userId)
     *
     * @param userId id of current User
     * @return filtered meals list of current User
     */
    private List<Meal> getFilteredOrderedList(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> mealRepository = repository.get(userId);
        if (mealRepository == null) {
            return Collections.emptyList();
        }
        return mealRepository.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

