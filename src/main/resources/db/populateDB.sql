DELETE
FROM meals;
DELETE
FROM user_roles;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, description, calories, date_time)
VALUES (100000, 'Завтрак', 450, '2020-10-17 09:12:00'),
       (100000, 'Обед', 800, '2020-10-17 12:15:00'),
       (100000, 'Ужин', 250, '2020-10-17 20:24:00'),
       (100000, 'Праздничный обед', 1860, '2020-10-19 14:52:00'),
       (100001, 'Завтрак', 200, '2020-10-17 09:01:00'),
       (100001, 'Полдник', 200, '2020-10-17 12:04:00'),
       (100001, 'Обед', 650, '2020-10-17 14:58:00'),
       (100001, 'Ужин', 150, '2020-10-17 22:03:00');