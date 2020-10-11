package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.MealsUtil.getFilteredTos;
import static ru.javawebinar.topjava.util.MealsUtil.getTos;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public List<MealTo> getAll() {
        log.info("getAll");
        return getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getFilterResults(String startDateStr, String endDateStr, String startTimeStr, String endTimeStr) {
        LocalDate startDate = startDateStr.equals("") ? LocalDate.MIN : LocalDate.parse(startDateStr);
        LocalDate endDate = endDateStr.equals("") ? LocalDate.MAX : LocalDate.parse(endDateStr);
        LocalTime startTime = startTimeStr.equals("") ? LocalTime.MIN : LocalTime.parse(startTimeStr);
        LocalTime endTime = endTimeStr.equals("") ? LocalTime.MAX : LocalTime.parse(endTimeStr);

        List<Meal> meals = service.getFilterResults(authUserId(), startDate, endDate);
        return getFilteredTos(meals, authUserCaloriesPerDay(), startTime, endTime);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(authUserId(), id);
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        return service.create(authUserId(), meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(authUserId(), id);
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id {}", meal, id);
        service.update(authUserId(), meal);
    }
}