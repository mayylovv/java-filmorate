package ru.yandex.practicum.filmorate.storage.dbstorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
public class DBGenreStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }

    @Override
    public Optional<Genre> getGenre(int id) {
        String sqlGenre = "SELECT * FROM genre WHERE genre_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlGenre, this::makeGenre, id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sqlGenres = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlGenres, this::makeGenre);
    }
}