package ru.javawebinar.topjava.util.formatters;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.Locale;

public class TimeFormatter implements Formatter<LocalTime> {

    @Override
    public LocalTime parse(String text, Locale locale) throws ParseException {
        return LocalTime.parse(text);
    }

    @Override
    public String print(LocalTime time, Locale locale) {
        return time.toString();
    }
}
