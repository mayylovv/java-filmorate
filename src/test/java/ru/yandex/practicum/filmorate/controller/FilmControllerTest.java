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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbstorage.DBFilmStorage;
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
public class FilmControllerTest {

    private final User person = new User(1, "person@ya.ru", "login", "name", LocalDate.of(2001, 1, 28));
    private final Film film = new Film(1, "Film", "historical adventure", LocalDate.of(2022, 1, 5), 134, new Mpa(1, "G"), null);
    private final Film film2 = new Film(2, "secondFilm", "very scary", LocalDate.of(2009, 5, 5), 111, new Mpa(2, "PG"), null);

    private final ObjectMapper objectMapper;
    private final DBFilmStorage dbFilmStorage;
    private final DBUserStorage dbUserStorage;
    private final MockMvc mockMvc;

    @Autowired
    public FilmControllerTest(ObjectMapper objectMapper, DBFilmStorage dbFilmStorage,
                              DBUserStorage dbUserStorage, MockMvc mockMvc) {
        this.objectMapper = objectMapper;
        this.dbFilmStorage = dbFilmStorage;
        this.dbUserStorage = dbUserStorage;
        this.mockMvc = mockMvc;
    }

    @Test
    void addFilm() throws Exception {
        mockMvc.perform(post("/films")
                                .content(objectMapper.writeValueAsString(film)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("historical adventure"))
                .andExpect(jsonPath("$.releaseDate").value("2022-01-05"))
                .andExpect(jsonPath("$.duration").value(134));
    }

    @Test
    void updateFilmTest() throws Exception {
        dbFilmStorage.createFilm(film2);
        mockMvc.perform(put("/films")
                                .content(objectMapper.writeValueAsString(film)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("historical adventure"))
                .andExpect(jsonPath("$.releaseDate").value("2022-01-05"))
                .andExpect(jsonPath("$.duration").value(134));
    }

    @Test
    void updateFilmNotFoundTest() throws Exception {
        film.setId(23);
        mockMvc.perform(put("/films")
                                .content(objectMapper.writeValueAsString(film))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteFilmTest() throws Exception {
        mockMvc.perform(delete("/films/3"))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(dbFilmStorage.getFilm(3).isEmpty()));
    }

    @Test
    void deleteFilmNotFoundTest() throws Exception {
        mockMvc.perform(delete("/films/45"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getFilmTest() throws Exception {
        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("historical adventure"))
                .andExpect(jsonPath("$.releaseDate").value("2022-01-05"))
                .andExpect(jsonPath("$.duration").value(134))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getFilmNotFoundTest() throws Exception {
        mockMvc.perform(get("/films/134"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getAllFilmsTest() throws Exception {
        dbFilmStorage.createFilm(film);
        mockMvc.perform(get("/films")).andExpect(status().isOk());
    }

    @Test
    void addLikeAndRemoveTest() throws Exception {
        dbUserStorage.createUser(person);
        dbFilmStorage.createFilm(film);
        mockMvc.perform(put("/films/1/like/1")).andExpect(status().isOk());
        mockMvc.perform(delete("/films/1/like/1")).andExpect(status().isOk());
    }

    @Test
    void addLikeNotFoundTest() throws Exception {
        mockMvc.perform(put("/films/29/like/2"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void removeLikeNotFoundTest() throws Exception {
        mockMvc.perform(delete("/films/16/like/34"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Фильм не найден",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void getBestFilmsTest() throws Exception {
        film2.setId(2);
        dbFilmStorage.updateFilm(film2);
        mockMvc.perform(put("/films/2/like/1")).andExpect(status().isOk());
        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(dbFilmStorage.getTopOfFilms(2).get(0).getName(), "secondFilm"))
                .andExpect(result -> assertEquals(dbFilmStorage.getTopOfFilms(2).get(1).getName(), "Film"));
    }
}