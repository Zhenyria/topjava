package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealServiceMapImpl;
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
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final AtomicLong idCounter = new AtomicLong(0);
    private static final MealServiceMapImpl service = new MealServiceMapImpl();
    static {
        service.create(new Meal(idCounter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        service.create(new Meal(idCounter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        service.create(new Meal(idCounter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        service.create(new Meal(idCounter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        service.create(new Meal(idCounter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        service.create(new Meal(idCounter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        service.create(new Meal(idCounter.incrementAndGet(), LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    private static final DateTimeFormatter OUTPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH);
    private static final String INSERT = "/insert.jsp";
    private static final String EDIT = "/edit.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("come in GET");

        String path = "/meals.jsp";
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("meals", MealsUtil.filteredByStreams(service.getAll(), LocalTime.MIN, LocalTime.MAX, 2000));
            request.setAttribute("dateFormat", OUTPUT_DATE_FORMAT);
        } else {
            if (action.equals("insert")) {
                path = INSERT;
            }
            if (action.equals("edit")) {
                path = EDIT;
                request.setAttribute("id", Long.parseLong(request.getParameter("id")));
            }
            if (action.equals("delete")) {
                service.delete(Long.parseLong(request.getParameter("id")));
                request.setAttribute("meals", MealsUtil.filteredByStreams(service.getAll(), LocalTime.MIN, LocalTime.MAX, 2000));
                request.setAttribute("dateFormat", OUTPUT_DATE_FORMAT);
            }
        }

        request.getRequestDispatcher(path).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("come in POST");

        String action = request.getParameter("action");
       if (action.equals("insert")) {
            LocalDateTime dateTime = LocalDateTime.from(INPUT_DATE_FORMAT.parse(request.getParameter("dateTime")));
            String description = request.getParameter("description");
            int calories = Integer.parseInt(request.getParameter("calories"));
            Meal meal = new Meal(idCounter.incrementAndGet(), dateTime, description, calories);
            service.create(meal);
        }

        if (action.equals("edit")) {
            long id = Long.parseLong(request.getParameter("id"));

            String strDateTime = request.getParameter("dateTime");
            LocalDateTime dateTime = strDateTime.equals("") ? null : LocalDateTime.from(INPUT_DATE_FORMAT.parse(strDateTime));

            String strDescription = request.getParameter("description");
            String description = strDescription.equals("") ? null : strDescription;

            String strCalories = request.getParameter("calories");
            int calories = strCalories.equals("") ? -1 : Integer.parseInt(strCalories);

            Meal meal = new Meal(id, dateTime, description, calories);
            service.update(meal);
        }

        request.setAttribute("meals", MealsUtil.filteredByStreams(service.getAll(), LocalTime.MIN, LocalTime.MAX, 2000));
        request.setAttribute("dateFormat", OUTPUT_DATE_FORMAT);
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}
