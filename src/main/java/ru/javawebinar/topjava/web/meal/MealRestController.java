package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController extends AbstractMealController {
    private static final Logger log = LoggerFactory.getLogger(MealRestController.class);

    public MealRestController(MealService service) {
        super(service);
    }

    public Meal get(int id) {
        log.info("get meal {} for user {}", id, authUserId());
        return super.get(id);
    }

    public void delete(int id) {
        log.info("delete meal {} for user {}", id, authUserId());
        super.delete(id);
    }

    public List<MealTo> getAll() {
        log.info("getAll for user {}", authUserId());
        return super.getAll();
    }

    public Meal create(Meal meal) {
        log.info("create {} for user {}", meal, authUserId());
        return super.create(meal);
    }

    public void update(Meal meal, int id) {
        log.info("update {} for user {}", meal, authUserId());
        super.update(meal, id);
    }

    public List<MealTo> getBetween(@Nullable LocalDate startDate, @Nullable LocalTime startTime,
                                   @Nullable LocalDate endDate, @Nullable LocalTime endTime) {
        log.info("getBetween dates({} - {}) time({} - {}) for user {}", startDate, endDate, startTime, endTime, authUserId());
        return super.getBetween(startDate, startTime, endDate, endTime);
    }
}