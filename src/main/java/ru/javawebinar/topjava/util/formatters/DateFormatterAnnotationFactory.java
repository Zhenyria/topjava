package ru.javawebinar.topjava.util.formatters;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalDate;
import java.util.Set;

public class DateFormatterAnnotationFactory implements AnnotationFormatterFactory<DateFormat> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Set.of(LocalDate.class);
    }

    @Override
    public Printer<?> getPrinter(DateFormat annotation, Class<?> fieldType) {
        return new DateFormatter();
    }

    @Override
    public Parser<?> getParser(DateFormat annotation, Class<?> fieldType) {
        return new DateFormatter();
    }
}
