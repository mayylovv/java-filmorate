package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            throw new ObjectNotFoundException("Фильм с таким id не найден");
        }
        return film;
    }

    public Film deleteFilm(int id) {
        if (filmStorage.getFilm(id) == null) {
            throw new ObjectNotFoundException("Фильм с таким id не найден");
        }
        log.info("Фильм с id: {} был удален", id);
        return filmStorage.deleteFilm(id);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film likeTheFilm(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new ObjectNotFoundException("Фильм с таким id не найден");
        }
        filmStorage.getFilm(filmId).getUsersLikes().add(userId);
        log.info("Пользователь с id: {} поставил лайк на фильма с id: {}", userId, filmId);
        return filmStorage.getFilm(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        if (filmStorage.getFilm(filmId) == null) {
            throw new ObjectNotFoundException("Фильм с таким id не найден");
        }
        if (!filmStorage.getFilm(filmId).getUsersLikes().contains(userId)) {
            throw new InternalServerException("Лайк под фильмом не стоит");
        }
        filmStorage.getFilm(filmId).getUsersLikes().remove(userId);
        log.info("Лайк был удален с фильма с id: {}", filmId);

        return filmStorage.getFilm(filmId);
    }

    public List<Film> getTopOfFilms(Integer count) {
        log.info("Топ 10 лучших фильмов по количеству лайков");
        return filmStorage.getAllFilms().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getUsersLikes().size(), o1.getUsersLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
