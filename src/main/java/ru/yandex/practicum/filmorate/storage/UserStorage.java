package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Optional<User> deleteUser(int id);

    Optional<User> getUser(int id);

    Collection<User> getAllUsers();

    List<Integer> addFriend(int idOfPerson1, int idOfPerson2);

    List<Integer> deleteFriend(int idOfPerson1, int idOfPerson2);

    List<User> getFriendsListOfPerson(int id);

    List<User> getListOfCommonFriends(int idOfPerson1, int idOfPerson2);
}
