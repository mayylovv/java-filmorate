package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public Optional<User> deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<Integer> addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<Integer> deleteFriends(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsListOfPerson(@PathVariable int id) {
        return userService.getFriendsListOfPerson(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getListOfCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getListOfCommonFriends(id, otherId);
    }

}
