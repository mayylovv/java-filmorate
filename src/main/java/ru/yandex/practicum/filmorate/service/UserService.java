package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUser(int id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + id + " не найден");
        }
        return userStorage.getUser(id);
    }

    public User deleteUser(int id) {
        if (userStorage.getUser(id) == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + id + " не найден");
        } else {
            log.info("Пользователь с id: {} был удален", id);
            return userStorage.deleteUser(id);
        }
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public List<User> addFriends(int idOfPerson1, int idOfPerson2) {
        if (userStorage.getUser(idOfPerson1) == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + idOfPerson1 + " не найден");
        }
        if (userStorage.getUser(idOfPerson2) == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + idOfPerson2 + " не найден");
        }
        if (userStorage.getUser(idOfPerson1).getFriends().contains(idOfPerson2)) {
            throw new InternalServerException("Пользователи уже являются друзьями");
        }
        userStorage.getUser(idOfPerson1).getFriends().add(idOfPerson2);
        userStorage.getUser(idOfPerson2).getFriends().add(idOfPerson1);
        log.info("Пользователи были успешно добавлены в список друзей");

        List<User> userList = new ArrayList<>();
        userList.add(userStorage.getUser(idOfPerson1));
        userList.add(userStorage.getUser(idOfPerson2));
        return userList;
    }

    public List<User> deleteFriends(int idOfPerson1, int idOfPerson2) {
        if (userStorage.getUser(idOfPerson1) == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + idOfPerson1 + " не найден");
        }
        if (userStorage.getUser(idOfPerson2) == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + idOfPerson2 + " не найден");
        }
        if (!userStorage.getUser(idOfPerson1).getFriends().contains(idOfPerson2)) {
            throw new InternalServerException("Пользователи не являются друзьями");
        }
        userStorage.getUser(idOfPerson1).getFriends().remove(idOfPerson2);
        userStorage.getUser(idOfPerson2).getFriends().remove(idOfPerson1);
        log.info("Пользователи были успешно удалены из списка друзей");

        List<User> userList = new ArrayList<>();
        userList.add(userStorage.getUser(idOfPerson1));
        userList.add(userStorage.getUser(idOfPerson2));
        return userList;
    }

    public List<User> getFriendsListOfPerson(int id) {
        if (userStorage.getUser(id) == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.info("Список друзей пользователя с id: {}", id);
        return userStorage.getUser(id).getFriends()
                .stream().map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getListOfCommonFriends(int idOfPerson1, int idOfPerson2) {
        if (userStorage.getUser(idOfPerson1) == null) {
            throw new ObjectNotFoundException("Пользователь с id = " + idOfPerson1 + " не найден");
        }
        if (userStorage.getUser(idOfPerson2) == null) {
            throw new ObjectNotFoundException("Пользователь с id =  " + idOfPerson2 + " не найден");
        }
        log.info("Список общих друзей пользователей с id: {} и {}", idOfPerson1, idOfPerson2);
        User firstPerson = userStorage.getUser(idOfPerson1);
        User secondPerson = userStorage.getUser(idOfPerson2);
        return firstPerson.getFriends().stream()
                .filter(friendId -> secondPerson.getFriends().contains(friendId))
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
