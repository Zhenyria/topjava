package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static ru.javawebinar.topjava.util.ValidationUtil.validate;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    /**
     * Special Extractor for extracting users with their roles from resultSet
     * after sql-request of type "user JOIN INNER user_roles"
     */
    private static final ResultSetExtractor<List<User>> resultSetExtractor = JdbcUserRepository::getMapForExtractor;

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
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users u LEFT JOIN user_roles ur ON u.id=ur.user_id WHERE u.id=?",
                resultSetExtractor, id);

        return users != null ? DataAccessUtils.singleResult(users) : null;
    }

    @Override
    public User getByEmail(String email) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id WHERE u.email=?",
                resultSetExtractor, email);

        return users != null ? DataAccessUtils.singleResult(users) : null;
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users u LEFT JOIN user_roles ur ON u.id = ur.user_id ORDER BY u.name, u.email",
                resultSetExtractor);
        return CollectionUtils.isEmpty(users) ? Collections.emptyList() : users;
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

    private static List<User> getMapForExtractor(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        for (int i = 0; rs.next(); i++) {
            int id = rs.getInt("id");
            String role = rs.getString("role");
            if (role == null) {
                users.add(new User(
                        id,
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("calories_per_day"),
                        rs.getBoolean("enabled"),
                        rs.getDate("registered"),
                        Collections.emptySet()));
                continue;
            }
            Role newRole = Role.valueOf(role);
            if (i > 0 && users.get(i - 1).getId() == id) {
                User user = users.get(i - 1);
                Set<Role> roles = user.getRoles();
                roles.add(newRole);
                user.setRoles(roles);
                i--;
            } else {
                users.add(new User(
                        id,
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("calories_per_day"),
                        rs.getBoolean("enabled"),
                        rs.getDate("registered"),
                        Set.of(newRole)));
            }
        }
        return users;
    }
}
