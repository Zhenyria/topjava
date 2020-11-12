package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.AbstractNamedEntity;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    /**
     * Special Extractors for extracting users with their roles from resultSet
     * after sql-request of type "user JOIN INNER user_roles"
     */
    private static final ResultSetExtractor<Map<String, User>> resultSetExtractor =
            resultSet -> getMapForExtractor(resultSet, "id");

    private static final ResultSetExtractor<Map<String, User>> resultSetExtractorForEmail =
            resultSet -> getMapForExtractor(resultSet, "email");

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        validate(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            saveRoles(user.getRoles(), user.getId());
        } else {
            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password,
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource) == 0) {
                return null;
            } else {
                deleteRoles(user.getId());
                saveRoles(user.getRoles(), user.getId());
            }
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        Map<String, User> users = jdbcTemplate.query(
                "SELECT * FROM users u INNER JOIN user_roles ur ON u.id=ur.user_id AND u.id=?",
                resultSetExtractor, id);

        return users != null ? users.get(String.valueOf(id)) : null;
    }

    @Override
    public User getByEmail(String email) {
        Map<String, User> users = jdbcTemplate.query(
                "SELECT * FROM users u INNER JOIN user_roles ur ON u.id = ur.user_id WHERE u.email=? ORDER BY u.name, u.email",
                resultSetExtractorForEmail, email);

        return users != null ? users.get(email) : null;
    }

    @Override
    public List<User> getAll() {
        Map<String, User> users = jdbcTemplate.query(
                "SELECT * FROM users u INNER JOIN user_roles ur ON u.id = ur.user_id", resultSetExtractor);

        if (users == null || users.size() <= 0) {
            return Collections.emptyList();
        }
        return users.values().stream()
                .sorted(Comparator.comparing((Function<User, String>) AbstractNamedEntity::getName).thenComparing(User::getEmail))
                .collect(Collectors.toList());
    }

    private void deleteRoles(int id) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", id);
    }

    private void saveRoles(Set<Role> roles, int id) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles(user_id, role) VALUES (?, ?)",
                roles,
                roles.size(),
                (ps, role) -> {
                    ps.setInt(1, id);
                    ps.setString(2, role.name());
                });
    }

    private static <T> Map<T, User> getMapForExtractor(ResultSet rs, String keyColumn) throws SQLException {
        Map<T, User> users = new HashMap<>();
        while (rs.next()) {
            String key = rs.getString(keyColumn);
            if (users.containsKey(key)) {
                users.get(key).getRoles().add(Role.valueOf(rs.getString("role")));
            } else {
                users.put((T) key,
                        new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getInt("calories_per_day"),
                                rs.getBoolean("enabled"),
                                rs.getDate("registered"),
                                Set.of(Role.valueOf(rs.getString("role")))));
            }
        }
        return users;
    }

    private void validate(User user) {
        Set<ConstraintViolation<User>> validateViolations = validator.validate(user);
        if (validateViolations.size() > 0) {
            throw new ConstraintViolationException(validateViolations);
        }
    }
}
