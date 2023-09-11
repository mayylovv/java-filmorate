package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private int id = 0;
    protected Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        id++;
        if (!users.containsKey(id)) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            user.setId(id);
            users.put(id, user);
        } else {
            throw new UserNotFoundException("Проблема с идентификатором пользователя");
        }
        log.info("Был добавлен пользователь {}", user.getName());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isEmpty()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
        } else {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        log.info("Информация о пользователе {} была обновлена", user.getName());
        return user;
    }

    @Override
    public User deleteUser(int id) {
        User user = users.get(id);
        users.remove(id);
        return user;
    }

    @Override
    public User getUser(int id) {
        User user = users.get(id);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
