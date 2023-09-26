package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> deleteFilm(int id);

    Optional<Film> getFilm(int id);

    Collection<Film> getAllFilms();

    Optional<Film> likeTheFilm(int filmId, int userId);

    Optional<Film> removeLike(int filmId, int userId);

    List<Film> getTopOfFilms(int count);
}
