package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    /**
     * Special Extractors for extracting users with their roles from resultSet
     * after sql-request of type "user JOIN INNER user_roles"
     */
    private static final ResultSetExtractor<Map<String, User>> RESULT_SET_EXTRACTOR =
            resultSet -> getMapForExtractor(resultSet, "id");

    private static final ResultSetExtractor<Map<String, User>> RESULT_SET_EXTRACTOR_WITH_EMAIL =
            resultSet -> getMapForExtractor(resultSet, "email");

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
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
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
                RESULT_SET_EXTRACTOR, id);

        return users != null ? users.get(String.valueOf(id)) : null;
    }

    @Override
    public User getByEmail(String email) {
        Map<String, User> users = jdbcTemplate.query(
                "SELECT * FROM users u INNER JOIN user_roles ur ON u.id = ur.user_id WHERE u.email=? ORDER BY u.name, u.email",
                RESULT_SET_EXTRACTOR_WITH_EMAIL, email);

        return users != null ? users.get(email) : null;
    }

    @Override
    public List<User> getAll() {
        Map<String, User> users = jdbcTemplate.query(
                "SELECT * FROM users u INNER JOIN user_roles ur ON u.id = ur.user_id ORDER BY u.name, u.email",
                RESULT_SET_EXTRACTOR);

        return users != null && users.size() > 0 ? new ArrayList<>(users.values()) : Collections.emptyList();
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
}
