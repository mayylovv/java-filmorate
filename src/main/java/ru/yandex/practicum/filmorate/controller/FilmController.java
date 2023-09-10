package ru.yandex.practicum.filmorate.controller;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    @Getter
    private int id = 0;
    public Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate firstFilm = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(firstFilm)) {
            throw new ValidationException("Дата релиза не может быть раньше" + firstFilm);
        }
        id++;
        if (!films.containsKey(id)) {
            film.setId(id);
            films.put(id, film);
        } else {
            throw new ValidationException("Проблема с идентификатором фильма");
        }
        log.info("Фильм {} был добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getReleaseDate().isBefore(firstFilm)) {
            throw new ValidationException("Дата релиза не может быть раньше" + firstFilm);
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ValidationException("Такой фильм не существует");
        }
        log.info("Информация о {} была обновлена", film.getName());
        return film;
    }
}