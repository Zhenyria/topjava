package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Comparator<Meal> comparator = (meal, meal1) -> meal1.getDateTime().compareTo(meal.getDateTime());

    {
        final int userId = 1;
        MealsUtil.meals.forEach(m -> save(userId, m));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        Map<Integer, Meal> userRepository = getUserRepository(userId);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userRepository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        return userRepository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int userId, int id) {
        return getUserRepository(userId).remove(id) != null;
    }

    @Override
    public Meal get(int userId, int id) {
        return getUserRepository(userId).get(id);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        List<Meal> meals = (List<Meal>) getAllUnordered(userId);
        meals.sort(comparator);
        return meals;
    }

    @Override
    public Collection<Meal> getFilterResults(int userId, LocalDate startDate, LocalDate endDate) {
        return getAllUnordered(userId).stream()
                .filter(m -> DateTimeUtil.isBetweenHalfOpen(m.getDate(), startDate, endDate))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Util method provide unordered Meal List for current user (by means of userId)
     *
     * @param userId id of current User
     * @return meals list of current User
     */
    private Collection<Meal> getAllUnordered(int userId) {
        return new ArrayList<>(getUserRepository(userId).values());
    }

    /**
     * Util method provide Meal repository for current User (by means of userId)
     *
     * @param userId id of current User
     * @return meals repository of current User
     */
    private Map<Integer, Meal> getUserRepository(int userId) {
        return repository.computeIfAbsent(userId, x -> new ConcurrentHashMap<>());
    }
}

