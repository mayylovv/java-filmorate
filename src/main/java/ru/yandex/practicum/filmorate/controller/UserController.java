package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

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

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public User deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriends(@PathVariable int idOfPerson1, @PathVariable int idOfPerson2) {
        return userService.addFriends(idOfPerson1, idOfPerson2);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriends(@PathVariable int idOfPerson1, @PathVariable int idOfPerson2) {
        return userService.deleteFriends(idOfPerson1, idOfPerson2);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsListOfPerson(@PathVariable int id) {
        return userService.getFriendsListOfPerson(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getListOfCommonFriends(@PathVariable int idOfPerson1, @PathVariable int idOfPerson2) {
        return userService.getListOfCommonFriends(idOfPerson1, idOfPerson2);
    }
}
