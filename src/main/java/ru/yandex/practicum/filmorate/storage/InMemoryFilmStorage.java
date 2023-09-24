package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 0;
    public Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate firstFilm = LocalDate.of(1895, 12, 28);

    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(firstFilm)) {
            throw new ValidationException("Дата релиза не может быть раньше" + firstFilm);
        }
        id++;
        if (!films.containsKey(id)) {
            film.setId(id);
            films.put(id, film);
        } else {
            throw new ObjectNotFoundException("Проблема с идентификатором фильма");
        }
        log.info("Фильм {} был добавлен", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isBefore(firstFilm)) {
            throw new ValidationException("Дата релиза не может быть раньше" + firstFilm);
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            throw new ObjectNotFoundException("Такой фильм не существует");
        }
        log.info("Информация о {} была обновлена", film.getName());
        return film;
    }

    @Override
    public Film deleteFilm(int id) {
        Film film = films.get(id);
        films.remove(id);
        return film;
    }

    @Override
    public Film getFilm(int id) {
        Film film = films.get(id);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }
}
