package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealMapDataBase {
    private final Map<Long, Meal> mealDb = new HashMap<>();

    public List<Meal> getAll() {
        return new ArrayList<>(mealDb.values());
    }

    public Meal get(long id) {
        return mealDb.get(id);
    }

    public void add(Meal meal) {
        mealDb.put(meal.getId(), meal);
    }

    public Meal update(Meal meal) {
        Meal oldMeal = get(meal.getId());
        Meal newMeal = new Meal(
                meal.getId(),
                meal.getDateTime() == null ? oldMeal.getDateTime() : meal.getDateTime(),
                meal.getDescription() == null ? oldMeal.getDescription() : meal.getDescription(),
                meal.getCalories() < 0 ? oldMeal.getCalories() : meal.getCalories()
        );
        mealDb.put(meal.getId(), newMeal);
        return newMeal;
    }

    public void delete(long id) {
        mealDb.remove(id);
    }
}
