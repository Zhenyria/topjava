package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

public interface MealService {

    /**
     * @return Meal list from database
     */
    List<Meal> getAll();

    /**
     * get Meal from database
     * @param id id of the current Meal
     * @return current Meal
     */
    Meal get(long id);

    /**
     * create new Meal in database
     * @return true if create was success, and false if create is failed
     */
    boolean create(Meal meal);

    /**
     * update Meal in database
     * @param meal Meal with updated properties
     */
    Meal update(Meal meal);

    /**
     * delete Meal in database
     * @param id id of the removed Meal
     * @return true if current Meal was remove and false if deleting is failed
     */
    boolean delete(long id);
}
