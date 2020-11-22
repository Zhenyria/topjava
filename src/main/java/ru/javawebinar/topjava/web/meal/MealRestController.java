package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {

    static final String REST_URL = "/rest/meals";

    @Override
    @GetMapping("/{id}")
    public Meal get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Meal> createWithLocation(@RequestBody Meal meal) {
        Meal created = super.create(meal);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Override
    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Meal meal, @PathVariable int id) {
        super.update(meal, id);
    }

    @GetMapping("/filter")
    public List<MealTo> getBetweenDateTime(@RequestParam(name = "start_date", required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   LocalDateTime startDate,
                                           @RequestParam(name = "start_time", required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   LocalDateTime startTime,
                                           @RequestParam(name = "end_date", required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   LocalDateTime endDate,
                                           @RequestParam(name = "end_time", required = false)
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                   LocalDateTime endTime) {
        return super.getBetween(
                startDate == null ? null : startDate.toLocalDate(),
                startTime == null ? null : startTime.toLocalTime(),
                endDate == null ? null : endDate.toLocalDate(),
                endTime == null ? null : endTime.toLocalTime());
    }
}