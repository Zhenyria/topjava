package ru.javawebinar.topjava.repository;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.slf4j.LoggerFactory.getLogger;

public class MealMapDataBase implements MealDataBase {
    private static final Logger log = getLogger(MealMapDataBase.class);
    private final Map<Long, Meal> mealDb = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    @Override
    public List<Meal> getAll() {
        log.debug("return all meals");
        return new ArrayList<>(mealDb.values());
    }

    @Override
    public Meal get(long id) {
        log.debug("get meal by means of id");
        return mealDb.get(id);
    }

    @Override
    public Meal create(Meal meal) {
        log.debug("create meal");
        long id = idCounter.getAndIncrement();
        return mealDb.put(id, new Meal(id, meal.getDateTime(), meal.getDescription(), meal.getCalories()));
    }

    @Override
    public Meal update(Meal meal) {
        log.debug("try to update meal");
        Meal oldMeal = mealDb.replace(meal.getId(), meal);
        if (oldMeal == null) {
            log.debug("meal is null");
            return null;
        }
        log.debug("meal is updated");
        return meal;
    }

    @Override
    public boolean delete(long id) {
        log.debug("delete meal");
        return mealDb.remove(id) != null;
    }
}
