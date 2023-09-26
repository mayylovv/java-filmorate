package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dbstorage.DBFilmStorage;
import ru.yandex.practicum.filmorate.storage.dbstorage.DBUserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmService {
    private final DBFilmStorage dbFilmStorage;
    private final DBUserStorage dbUserStorage;

    @Autowired
    public FilmService(DBFilmStorage dbFilmStorage, DBUserStorage dbUserStorage) {
        this.dbFilmStorage = dbFilmStorage;
        this.dbUserStorage = dbUserStorage;
    }

    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Указана некорректная дата");
        }
        Film createFilm = dbFilmStorage.createFilm(film);
        log.info("Фильм был добавлен");
        return createFilm;
    }

    public Film updateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Указана некорректная дата");
        }
        if (dbFilmStorage.getFilm(film.getId()).isEmpty()) {
            throw new ObjectNotFoundException("Фильм не найден");
        }
        Film updateFilm = dbFilmStorage.updateFilm(film);
        log.info("Фильм {} был обновлен", film.getId());
        return updateFilm;
    }

    public Optional<Film> deleteFilm(int id) {
        if (dbFilmStorage.getFilm(id).isEmpty()) {
            throw new ObjectNotFoundException("Фильм не найден");
        }
        Optional<Film> deletedFilm = dbFilmStorage.deleteFilm(id);
        log.info("Фильм с id = {} был удален", id);
        return deletedFilm;
    }

    public Optional<Film> getFilm(int id) {
        if (dbFilmStorage.getFilm(id).isEmpty()) {
            throw new ObjectNotFoundException("Фильм не найден");
        }
        Optional<Film> film = dbFilmStorage.getFilm(id);
        log.info("Фильм с id = {} передан", id);
        return film;
    }

    public Collection<Film> getAllFilms() {
        Collection<Film> films = dbFilmStorage.getAllFilms();
        log.info("Список фильмов передан.");
        return films;
    }

    public Optional<Film> likeTheFilm(int filmId, int userId) {
        if (dbFilmStorage.getFilm(filmId).isEmpty()) {
            throw new ObjectNotFoundException("Фильм не найден");
        }
        if (dbUserStorage.getUser(userId).isEmpty()) {
            throw new ObjectNotFoundException("Фильм или пользователь не найдены");
        }
        Optional<Film> likedFilm = dbFilmStorage.likeTheFilm(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        return likedFilm;
    }

    public Optional<Film> removeLike(int filmId, int userId) {
        if (dbFilmStorage.getFilm(filmId).isEmpty()) {
            throw new ObjectNotFoundException("Фильм не найден");
        }
        if (dbUserStorage.getUser(userId).isEmpty()) {
            log.info("Пользователь {} не найден.", userId);
            throw new ObjectNotFoundException("Фильм или пользователь не найдены");
        }
        Optional<Film> filmWithoutLike = dbFilmStorage.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк к фильму {}", userId, filmId);
        return filmWithoutLike;
    }

    public List<Film> getTopOfFilms(int count) {
        List<Film> bestFilms = dbFilmStorage.getTopOfFilms(count);
        log.info("Отправлен список из {} самых популярных фильмов", count);
        return bestFilms;
    }

}
