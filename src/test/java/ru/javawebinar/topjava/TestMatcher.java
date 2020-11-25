package ru.javawebinar.topjava;

import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.TestUtil.readListFromJsonMvcResult;

public class TestMatcher<T> {
    private final Class<T> clazz;
    private final boolean isUseEqual;
    private final String[] fieldsToIgnore;

    private TestMatcher(Class<T> clazz, boolean isUseEqual, String... fieldsToIgnore) {
        this.clazz = clazz;
        this.isUseEqual = isUseEqual;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    public static <T> TestMatcher<T> usingEquals(Class<T> clazz) {
        return new TestMatcher<>(clazz, true);
    }

    public static <T> TestMatcher<T> usingIgnoringFieldsComparator(Class<T> clazz, String... fieldsToIgnore) {
        return new TestMatcher<>(clazz, false, fieldsToIgnore);
    }

    public void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, List.of(expected));
    }

    public void assertMatch(T actual, T expected) {
        if (isUseEqual) {
            assertThat(actual).isEqualTo(expected);
        } else {
            assertThat(actual).usingRecursiveComparison().ignoringFields(fieldsToIgnore).isEqualTo(expected);
        }
    }

    public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        if (isUseEqual) {
            assertThat(actual).isEqualTo(expected);
        } else {
            assertThat(actual).usingElementComparatorIgnoringFields(fieldsToIgnore).isEqualTo(expected);
        }
    }

    public ResultMatcher contentJson(T expected) {
        return result -> assertMatch(TestUtil.readFromJsonMvcResult(result, clazz), expected);
    }

    public ResultMatcher contentJson(T... expected) {
        return contentJson(List.of(expected));
    }

    public ResultMatcher contentJson(Iterable<T> expected) {
        return result -> assertMatch(readListFromJsonMvcResult(result, clazz), expected);
    }
}
