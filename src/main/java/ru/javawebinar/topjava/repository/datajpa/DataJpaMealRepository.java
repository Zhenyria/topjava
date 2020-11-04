package ru.javawebinar.topjava.repository.datajpa;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class DataJpaMealRepository implements MealRepository {

    private final CrudMealRepository mealRepository;

    private final CrudUserRepository userRepository;

    public DataJpaMealRepository(CrudMealRepository mealRepository, CrudUserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setUser(userRepository.getOne(userId));
            return mealRepository.save(meal);
        }
        Meal currentMeal = get(meal.getId(), userId);
        if (currentMeal == null) {
            return null;
        }
        currentMeal.setDateTime(meal.getDateTime());
        currentMeal.setCalories(meal.getCalories());
        currentMeal.setDescription(meal.getDescription());
        return currentMeal;
    }

    @Override
    public boolean delete(int id, int userId) {
        return mealRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        return mealRepository
                .findById(id)
                .filter(m -> m.getId() == id && m.getUser().getId() == userId)
                .orElse(null);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return mealRepository.getAll(userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return mealRepository.getBetween(startDateTime, endDateTime, userId);
    }

    @Transactional
    @Override
    public Meal getWithUser(int id, int userId) {
        Meal meal = get(id, userId);
        if (meal == null) {
            return null;
        }
        meal.setUser((User) Hibernate.unproxy(meal.getUser()));
        return meal;
    }
}
