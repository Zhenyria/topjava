package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Meal m WHERE m.id=:id AND m.user.id=:user_id")
    int delete(@Param("id") int id, @Param("user_id") int userId);

    @Query("""
            SELECT m FROM Meal m 
            WHERE m.user.id=:user_id AND m.dateTime>=:start_date_time AND m.dateTime<:end_date_time ORDER BY m.dateTime DESC
            """)
    List<Meal> getBetween(@Param("start_date_time") LocalDateTime startDateTime, @Param("end_date_time") LocalDateTime endDateTime, @Param("user_id") int userId);

    @Query("""
            SELECT m FROM Meal m
            WHERE m.user.id=:user_id ORDER BY m.dateTime DESC
            """)
    List<Meal> getAll(@Param("user_id") int userId);

    @Query("""
            SELECT COUNT(1)
            FROM Meal m
            WHERE m.id=:id AND m.user.id=:user_id
            """)
    int isExist(@Param("id") int id, @Param("user_id") int userId);
}
