package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.SecurityUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
@RequestMapping("/meals")
public class MealJspController extends AbstractMealController {
    private static final Logger log = LoggerFactory.getLogger(MealJspController.class);

    public MealJspController(MealService service) {
        super(service);
    }

    @GetMapping()
    public String getAll(HttpServletRequest request) {
        log.info("getAll for user {}", authUserId());
        request.setAttribute("meals", super.getAll());
        return "meals";
    }

    @GetMapping(params = {"action=filter"})
    public String getAllFiltered(HttpServletRequest request) {
        log.info("getAllFiltered for user {}", SecurityUtil.authUserId());
        request.setAttribute("meals", super.getBetween(
                parseLocalDate(request.getParameter("startDate")),
                parseLocalTime(request.getParameter("startTime")),
                parseLocalDate(request.getParameter("endDate")),
                parseLocalTime(request.getParameter("endTime"))));
        return "meals";
    }

    @GetMapping(params = {"action=create"})
    public String getCreate(HttpServletRequest request) {
        log.info("start creating for user {}", authUserId());
        request.setAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @GetMapping(params = {"action=update"})
    public String getUpdate(HttpServletRequest request) {
        log.info("start updating for user {}", authUserId());
        request.setAttribute("meal", service.get(getId(request), SecurityUtil.authUserId()));
        return "mealForm";
    }

    @GetMapping(params = {"action=delete"})
    public String delete(HttpServletRequest request) {
        int id = getId(request);
        log.info("delete meal {} for user {}", id, authUserId());
        super.delete(id);
        return "redirect:meals";
    }

    @PostMapping()
    public String save(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (!StringUtils.hasText(request.getParameter("id"))) {
            log.info("create meal for user {}", authUserId());
            super.create(meal);
        } else {
            int id = getId(request);
            log.info("update meal {} for user {}", id, authUserId());
            super.update(meal, id);
        }
        return "redirect:meals";
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }
}
