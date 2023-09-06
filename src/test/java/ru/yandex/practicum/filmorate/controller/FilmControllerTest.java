package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    Film film;
    FilmController filmController;

    @BeforeEach
    void filmControllerInit() {
        filmController = new FilmController();
        film = new Film("Начало", "Научная фантастика/Боевик", LocalDate.of(2010, 07, 22), 148);
        filmController.createFilm(film);
    }

    @Test
    void createFilmTest() {
        Film movie = filmController.films.get(film.getId());
        assertEquals(film, movie);
    }

    @Test
    void incorrectDateFilmTest() {
        Film film2 = new Film("Интерстеллар", "Приключения/Научная фантастика", LocalDate.of(1014, 11, 6), 169);
        assertThrows(ValidationException.class, () -> filmController.createFilm(film2));
    }
}
