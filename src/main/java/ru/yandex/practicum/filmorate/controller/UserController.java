package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Getter
    private int id = 0;
    protected Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }


    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        id++;
        if (!users.containsKey(id)) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            user.setId(id);
            users.put(id, user);
        } else {
            throw new ValidationException("проблема с идентификатором пользователя");
        }
        log.info("Был добавлен пользователь {}", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
        } else {
            throw new ValidationException("Пользователь не найден.");
        }
        log.info("Информация о пользователе {} была обновлена", user.getName());
        return user;
    }
}
