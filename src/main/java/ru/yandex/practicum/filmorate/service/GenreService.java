package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dbstorage.DBGenreStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {

    private final DBGenreStorage dbGenreStorage;

    @Autowired
    public GenreService(DBGenreStorage dbGenreStorage) {
        this.dbGenreStorage = dbGenreStorage;
    }

    public Optional<Genre> getGerne(int id) {
        Optional<Genre> genre = dbGenreStorage.getGenre(id);
        if (genre.isEmpty()) {
            throw new ObjectNotFoundException("Жанр с id = " + id + " не был найден");
        }
        log.info("Жанр представлен");
        return genre;
    }

    public Collection<Genre> getAllGernes() {
        Collection<Genre> allGenres = dbGenreStorage.getAllGenres();
        if (allGenres.isEmpty()) {
            throw new ObjectNotFoundException("Жанры не найдены");
        }
        log.info("Список жанров представлен");
        return allGenres;
    }


}
