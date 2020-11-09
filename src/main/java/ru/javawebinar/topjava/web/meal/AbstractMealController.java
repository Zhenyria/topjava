package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

abstract public class AbstractMealController {

    @Autowired
    protected final MealService service;

    public AbstractMealController(MealService service) {
        this.service = service;
    }

    public Meal get(int id) {
        return service.get(id, authUserId());
    }

    public void delete(int id) {
        service.delete(id, authUserId());
    }

    public List<MealTo> getAll() {
        return MealsUtil.getTos(service.getAll(authUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public Meal create(Meal meal) {
        checkNew(meal);
        return service.create(meal, authUserId());
    }

    public void update(Meal meal, int id) {
        assureIdConsistent(meal, id);
        service.update(meal, authUserId());
    }

    /**
     * <ol>Filter separately
     * <li>by date</li>
     * <li>by time for every date</li>
     * </ol>
     */
    public List<MealTo> getBetween(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                   @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        return MealsUtil.getFilteredTos(
                service.getBetweenInclusive(startDate, endDate, authUserId()),
                SecurityUtil.authUserCaloriesPerDay(), startTime, endTime
        );
    }
}
