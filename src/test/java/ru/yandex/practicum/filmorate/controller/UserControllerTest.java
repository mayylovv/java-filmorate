package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbstorage.DBUserStorage;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class UserControllerTest {

    private final User person1 = new User(1, "person1@ya.ru", "login", "name", LocalDate.of(2001, 1, 28));
    private final User person2 = new User(2, "person2@ya.ru", "new_login", "name", LocalDate.of(1990, 1, 26));

    private final ObjectMapper objectMapper;
    private final DBUserStorage dbUserStorage;
    private final MockMvc mockMvc;

    @Autowired
    public UserControllerTest(ObjectMapper objectMapper, DBUserStorage dbUserStorage, MockMvc mockMvc) {
        this.objectMapper = objectMapper;
        this.dbUserStorage = dbUserStorage;
        this.mockMvc = mockMvc;
    }

    @Test
    void addUserTest() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(person1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.login").value("login"))
                .andExpect(jsonPath("$.birthday").value("2001-01-28"))
                .andExpect(jsonPath("$.email").value("person1@ya.ru"));
    }

    @Test
    void updateUserTest() throws Exception {
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(person2)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("person2@ya.ru"))
                .andExpect(jsonPath("$.login").value("new_login"));
        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("person2@ya.ru"))
                .andExpect(jsonPath("$.login").value("new_login"));
    }

    @Test
    void updateUserNotFoundTest() throws Exception {
        person2.setId(545);
        mockMvc.perform(put("/users")
                                .content(objectMapper.writeValueAsString(person2)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        dbUserStorage.createUser(person1);
        dbUserStorage.createUser(person1);
        mockMvc.perform(delete("/users/3")).andExpect(status().isOk());
    }

    @Test
    void deleteUserNotFoundExceptionTest() throws Exception {
        mockMvc.perform(delete("/users/40"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getUserNotFoundExceptionTest() throws Exception {
        mockMvc.perform(get("/users/21"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getAllUsersTest() throws Exception {
        dbUserStorage.createUser(person1);
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

    @Test
    void addFriendsTest() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(2, dbUserStorage.getFriendsListOfPerson(1).get(0).getId()));
    }

    @Test
    void addFriendsNotFoundTest() throws Exception {
        mockMvc.perform(put("/users/9/friends/23"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeFriendsTest() throws Exception {
        dbUserStorage.createUser(person2);
        dbUserStorage.addFriend(2, 1);
        mockMvc.perform(delete("/users/2/friends/1"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(0, dbUserStorage.getFriendsListOfPerson(2).size()));
    }

    @Test
    void getFriendsListByIdTest() throws Exception {
        dbUserStorage.addFriend(1, 2);
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(1, dbUserStorage.getFriendsListOfPerson(1).size()));
        dbUserStorage.deleteFriend(1, 2);
    }

    @Test
    void getFriendsListOfPersonNotFoundTest() throws Exception {
        mockMvc.perform(get("/users/34/friends"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getCommonFriendsListTest() throws Exception {
        dbUserStorage.addFriend(4, 2);
        dbUserStorage.addFriend(1, 2);
        mockMvc.perform(get("/users/1/friends/common/4"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(2, dbUserStorage.getFriendsListOfPerson(1).get(0).getId()))
                .andExpect(result -> assertEquals(2, dbUserStorage.getFriendsListOfPerson(4).get(0).getId()));
        dbUserStorage.deleteFriend(4, 2);
        dbUserStorage.deleteFriend(1, 2);
    }

    @Test
    void getCommonFriendsNotFoundTest() throws Exception {
        mockMvc.perform(get("/users/9/friends/common/23"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Пользователь не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
