package ru.javawebinar.topjava.service;

import org.springframework.stereotype.Service;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
public class MealService {
    private MealRepository repository;
    private final static String excMessage = "meal %s is not found or does not belong to current user";

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(int userId, Meal meal) {
        Meal newMeal = repository.save(userId, meal);
        if (newMeal == null) {
            throw new NotFoundException(String.format(excMessage, ""));
        }
        return newMeal;
    }

    public void delete(int userId, int id) {
        if (!repository.delete(userId, id)) {
            throw new NotFoundException(String.format(excMessage, id));
        }
    }

    public Meal get(int userId, int id) {
        Meal meal = repository.get(userId, id);
        if (meal == null) {
            throw new NotFoundException(String.format(excMessage, id));
        }
        return meal;
    }

    public List<Meal> getAll(int userId) {
        return (List<Meal>) repository.getAll(userId);
    }

    public List<Meal> getFilterResults(int userId, LocalDate startDate, LocalDate endDate) {
        return (List<Meal>) repository.getFilterResults(userId, startDate, endDate);
    }

    public void update(int userId, Meal meal) {
        if (repository.save(userId, meal) == null) {
            throw new NotFoundException(String.format(excMessage, meal.getId()));
        }
    }
}