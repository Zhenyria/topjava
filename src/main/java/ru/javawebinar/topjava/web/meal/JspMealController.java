package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
@RequestMapping("/meals")
public class JspMealController extends AbstractMealController {

    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping()
    public String getAll(Model model) {
        log.info("getAll for user {}", authUserId());
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @GetMapping("filter")
    public String getAllFiltered(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            Model model) {
        log.info("getAllFiltered for user {}", SecurityUtil.authUserId());
        model.addAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }

    @GetMapping("create")
    public String getCreate(Model model) {
        log.info("start creating for user {}", authUserId());
        model.addAttribute("meal", new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @GetMapping("update")
    public String getUpdate(@RequestParam String id, Model model) {
        log.info("start updating for user {}", authUserId());
        model.addAttribute("meal", service.get(Integer.parseInt(id), SecurityUtil.authUserId()));
        return "mealForm";
    }

    @GetMapping("delete")
    public String delete(@RequestParam(name = "id") String strId) {
        int id = Integer.parseInt(strId);
        log.info("delete meal {} for user {}", id, authUserId());
        super.delete(id);
        return "redirect:/meals";
    }

    @PostMapping()
    public String save(@RequestParam(name = "id", required = false) String strId,
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
                       @RequestParam String description,
                       @RequestParam String calories) throws UnsupportedEncodingException {
        Meal meal = new Meal(dateTime, description, Integer.parseInt(calories));

        if (!StringUtils.hasText(strId)) {
            log.info("create meal for user {}", authUserId());
            super.create(meal);
        } else {
            int id = Integer.parseInt(strId);
            log.info("update meal {} for user {}", id, authUserId());
            super.update(meal, id);
        }
        return "redirect:/meals";
    }
}
