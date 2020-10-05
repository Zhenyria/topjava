package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealMapDataBase;

import java.util.List;

public class MealServiceMapImpl implements MealService {
    private static final MealMapDataBase repository = new MealMapDataBase();

    @Override
    public List<Meal> getAll() {
        return repository.getAll();
    }

    @Override
    public Meal get(long id) {
        return repository.get(id);
    }

    @Override
    public boolean create(Meal meal) {
        repository.add(meal);
        return true;
    }

    @Override
    public Meal update(Meal meal) {
        return repository.update(meal);
    }

    @Override
    public boolean delete(long id) {
        repository.delete(id);
        return true;
    }
}
