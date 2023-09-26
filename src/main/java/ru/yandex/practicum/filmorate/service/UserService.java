package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbstorage.DBUserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final DBUserStorage dbUserStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(DBUserStorage dbUserStorage, JdbcTemplate jdbcTemplate) {
        this.dbUserStorage = dbUserStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public User createUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        User createdUser = dbUserStorage.createUser(user);
        log.info("Пользователь был добавлен");
        return createdUser;
    }

    public User updateUser(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (dbUserStorage.getUser(user.getId()).isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        User updatedUser = dbUserStorage.updateUser(user);
        log.info("Пользователь c id = {} был обновлен", user.getId());
        return updatedUser;
    }

    public Optional<User> deleteUser(int id) {
        if (dbUserStorage.getUser(id).isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        Optional<User> deletedUser = dbUserStorage.deleteUser(id);
        log.info("Пользователь с id = {} был удален", id);
        return deletedUser;
    }

    public Optional<User> getUser(int id) {
        Optional<User> user = dbUserStorage.getUser(id);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        log.info("Пользователь с id = {} был представлен", id);
        return user;
    }

    public Collection<User> getAllUsers() {
        Collection<User> allUsers = dbUserStorage.getAllUsers();
        log.info("Список пользователей был представлен");
        return allUsers;
    }

    public List<Integer> addFriend(int idOfPerson1, int idOfPerson2) {
        if (getUser(idOfPerson1).isEmpty() || getUser(idOfPerson2).isEmpty()) {
            throw new ObjectNotFoundException("Пользователи не найдены");
        }
        String checkFriendship = "SELECT * FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(checkFriendship, idOfPerson1, idOfPerson2);
        if (sqlRowSet.first()) {
            throw new InternalServerException("Пользователи уже являются друзьями");
        }
        List<Integer> result = dbUserStorage.addFriend(idOfPerson1, idOfPerson2);
        log.info("Пользователи {} и {} являются друзьями", idOfPerson1, idOfPerson2);
        return result;
    }

    public List<Integer> deleteFriends(int idOfPerson1, int idOfPerson2) {
        String checkFriendship = "SELECT * FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(checkFriendship, idOfPerson1, idOfPerson2);
        if (!sqlRowSet.first()) {
            throw new InternalServerException("Пользователь не подписан");
        }
        List<Integer> result = dbUserStorage.deleteFriend(idOfPerson1, idOfPerson2);
        log.info("Пользователи {} и {} теперь не являются друзьями", idOfPerson1, idOfPerson2);
        return result;
    }

    public List<User> getFriendsListOfPerson(int id) {
        if (dbUserStorage.getUser(id).isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        List<User> friendsListOfPerson = dbUserStorage.getFriendsListOfPerson(id);
        log.info("Представлен список друзей пользователя с id = {}", id);
        return friendsListOfPerson;
    }

    public List<User> getListOfCommonFriends(int idOfPerson1, int idOfPerson2) {
        if (dbUserStorage.getUser(idOfPerson1).isEmpty() || dbUserStorage.getUser(idOfPerson2).isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        List<User> listOfCommonFriends = dbUserStorage.getListOfCommonFriends(idOfPerson1, idOfPerson2);
        log.info("Список общих друзей {} и {} отправлен", idOfPerson1, idOfPerson2);
        return listOfCommonFriends;
    }
}
