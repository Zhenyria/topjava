package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-repository.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL_ID, USER_ID);
        assertMatch(meal, meal1forUser);
    }

    @Test
    public void getNotExistMeal() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_EXIST_MEAL_ID, USER_ID));
    }

    @Test
    public void getNotOwnedMeal() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(MEAL_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID, USER_ID));
    }

    @Test
    public void deleteNotExistMeals() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_EXIST_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteNotOwnedMeal() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = service.getBetweenInclusive(LocalDate.of(2020, 10, 17), LocalDate.of(2020, 10, 17), USER_ID);
        assertMatch(meals, meal3forUser, meal2forUser, meal1forUser);
    }

    @Test
    public void getBetweenNull() {
        List<Meal> meals = service.getBetweenInclusive(null, null, USER_ID);
        assertMatch(meals, meal4forUser, meal3forUser, meal2forUser, meal1forUser);
    }

    @Test
    public void getNotExistMealsBetweenInclusive() {
        assertMatch(Collections.emptyList(), service.getBetweenInclusive(LocalDate.of(2020, 10, 18), LocalDate.of(2020, 10, 18), USER_ID));
    }

    @Test
    public void getAll() {
        List<Meal> meals = service.getAll(USER_ID);
        assertMatch(meals, meal4forUser, meal3forUser, meal2forUser, meal1forUser);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(getUpdated(), USER_ID);
        assertMatch(service.get(updated.getId(), USER_ID), updated);
    }

    @Test
    public void updateNotOwnedMeal() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Meal newMeal = getNew();
        newMeal.setId(created.getId());
        assertMatch(created, newMeal);
        assertMatch(service.get(newMeal.getId(), USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(meal1forUser.getDateTime(), "Новая еда", 1200), USER_ID));
    }
}