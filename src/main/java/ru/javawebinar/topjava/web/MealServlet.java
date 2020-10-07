package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealDataBase;
import ru.javawebinar.topjava.repository.MealMapDataBase;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final int caloriesPerDay = 2000;
    private static final long insertId = -1;
    private static final DateTimeFormatter outputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final String edit = "/edit.jsp";
    private static final String meals = "/meals.jsp";
    private static final String mealsRedirect = "meals";

    private MealDataBase db;

    @Override
    public void init() {
        db = new MealMapDataBase();
        db.create(new Meal(insertId, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        db.create(new Meal(insertId, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        db.create(new Meal(insertId, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        db.create(new Meal(insertId, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        db.create(new Meal(insertId, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        db.create(new Meal(insertId, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        db.create(new Meal(insertId, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GET method request");
        String action = request.getParameter("action");
        String path = meals;

        if (action == null) {
            log.debug("GET: meal list loading");
            request.setAttribute("meals", MealsUtil.filteredByStreams(db.getAll(), LocalTime.MIN, LocalTime.MAX, caloriesPerDay));
            request.setAttribute("dateFormat", outputDateFormat);
        } else {
            switch (action) {
                case "edit":
                    log.debug("GET: start edit Meal");
                    request.setAttribute("meal", db.get(parseId(request)));
                    path = edit;
                    break;
                case "insert":
                    log.debug("GET: start create Meal");
                    path = edit;
                    break;
                case "delete":
                    log.debug("GET: delete Meal");
                    db.delete(parseId(request));
                default:
                    log.debug("GET: redirect to /meals");
                    response.sendRedirect(mealsRedirect);
                    return;
            }
        }
        log.debug("GET: forward to " + path);
        request.getRequestDispatcher(path).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("POST method request");
        request.setCharacterEncoding("UTF-8");

        Meal meal = createMeal(request);
        if (meal.getId() == -1) {
            log.debug("POST: create Meal");
            db.create(meal);
        } else {
            log.debug("POST: update Meal");
            db.update(meal);
        }
        log.debug("POST: redirect to /meals");
        response.sendRedirect(mealsRedirect);
    }

    private long parseId(HttpServletRequest request) {
        return Long.parseLong(request.getParameter("id"));
    }

    private Meal createMeal(HttpServletRequest request) {
        long id = parseId(request);
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        return new Meal(id, dateTime, description, calories);
    }
}
