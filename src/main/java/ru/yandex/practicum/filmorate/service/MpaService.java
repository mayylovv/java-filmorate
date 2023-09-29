package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dbstorage.DBMpaStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class MpaService {

    private final DBMpaStorage dbMpaStorage;

    @Autowired
    public MpaService(DBMpaStorage dbMpaStorage) {
        this.dbMpaStorage = dbMpaStorage;
    }

    public Optional<Mpa> getMpa(int id) {
        Optional<Mpa> mpa = dbMpaStorage.getMpa(id);
        if (mpa.isEmpty()) {
            throw new ObjectNotFoundException("Рейтинг c id = " + " не найден");
        }
        log.info("Рейтинг представлен");
        return mpa;
    }

    public Collection<Mpa> getAllMpa() {
        Collection<Mpa> allMpa = dbMpaStorage.getAllMpa();
        if (allMpa.isEmpty()) {
            throw new ObjectNotFoundException("Рейтинги не найдены");
        }
        log.info("Список рейтингов представлен");
        return allMpa;
    }
}