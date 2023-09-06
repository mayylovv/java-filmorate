package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    void UserControllerInit() {
        user = new User("test@yandex.ru", "TestLogin", LocalDate.of(1999, 01, 01));
        userController = new UserController();
        userController.createUser(user);
    }

    @Test
    void createUserTest() {
        user.setName("Test");
        User user1 = userController.users.get(user.getId());
        assertEquals(user, user1);
    }

    @Test
    void updateUserTest() {
        user.setName("Test");
        userController.updateUser(user);
        String name1  = userController.users.get(user.getId()).getName();
        assertEquals("Test", name1);
    }

    @Test
    void addNameNullTest() {
        String name = userController.users.get(user.getId()).getName();
        assertEquals("TestLogin", name);
    }
}
