package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DBFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DBGenreStorage dbGenreStorage;
    private final DBMpaStorage dbMpaStorage;

    @Autowired
    public DBFilmStorage(JdbcTemplate jdbcTemplate, DBGenreStorage dbGenreStorage, DBMpaStorage dbMpaStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.dbGenreStorage = dbGenreStorage;
        this.dbMpaStorage = dbMpaStorage;
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlFilm = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlFilm, new String[]{"id"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setLong(4, film.getDuration());
            return preparedStatement;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        String sqlMpa = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
        if (film.getMpa() != null) {
            jdbcTemplate.update(sqlMpa, film.getId(), film.getMpa().getId());
            film.setMpa(findMpa(film.getId()));
        }
        String sqlGerne = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(g -> jdbcTemplate.update(sqlGerne, film.getId(), g.getId()))
                    .collect(Collectors.toSet());
            film.setGenres(findGenres(film.getId()));
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        if (film.getMpa() != null) {
            String deleteMpa = "DELETE FROM mpa_films WHERE film_id = ?";
            String updateMpa = "INSERT INTO mpa_films (film_id, mpa_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteMpa, film.getId());
            jdbcTemplate.update(updateMpa, film.getId(), film.getMpa().getId());
            film.setMpa(findMpa(film.getId()));
        }
        if (film.getGenres() != null) {
            String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
            String updateGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteGenres, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenres, film.getId(), g.getId());
                }
                film.setGenres(findGenres(film.getId()));
            }
        }
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());
        return film;
    }

    @Override
    public Optional<Film> deleteFilm(int id) {

        Optional<Film> film = getFilm(id);

        String genreSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(genreSql, id);
        String mpaSql = "DELETE FROM mpa_films WHERE film_id = ?";
        jdbcTemplate.update(mpaSql, id);
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);

        return film;
    }

    @Override
    public Optional<Film> getFilm(int id) {
        String sqlFilm = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.duration, m.id as mpa_Id, m.name as mpa_Name" +
                " FROM films f JOIN mpa_films mf ON f.id = mf.film_id JOIN mpa m ON mf.MPA_ID = m.id WHERE f.ID = ? ";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlFilm, this::makeFilm, id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sqlFilms = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.duration, m.id as mpa_Id, m.name as mpa_Name" +
                " FROM films f JOIN mpa_films mf ON f.id = mf.film_id JOIN mpa m ON mf.mpa_id = m.id";
        return jdbcTemplate.query(sqlFilms, this::makeFilm);
    }


    @Override
    public Optional<Film> likeTheFilm(int filmId, int userId) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilm(filmId);
    }

    @Override
    public Optional<Film> removeLike(int filmId, int userId) {
        String sql = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilm(filmId);
    }

    @Override
    public List<Film> getTopOfFilms(int count) {
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.duration, m.id as mpa_Id, m.name as mpa_Name" +
                " FROM films f LEFT JOIN films_likes fl ON f.id = fl.film_id JOIN mpa_films mf ON f.id = mf.film_id " +
                " JOIN mpa m ON mf.mpa_id = m.id group by f.id, fl.film_id IN ( SELECT film_id FROM films_likes ) " +
                "ORDER BY COUNT(fl.film_id) DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        int mpaId = resultSet.getInt("mpa_Id");
        String mpaName = resultSet.getString("mpa_Name");
        Mpa mpa = new Mpa(mpaId, mpaName);

        return new Film(id,
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getLong("duration"),
                mpa,
                findGenres(id));
    }

    private List<Genre> findGenres(int filmId) {
        String genresSql = "SELECT genre.genre_id, name FROM genre " +
                "LEFT JOIN FILM_GENRE FG on genre.genre_id = FG.GENRE_ID WHERE film_id = ?";
        return jdbcTemplate.query(genresSql, dbGenreStorage::makeGenre, filmId);
    }

    private Mpa findMpa(int filmId) {
        String mpaSql = "SELECT id, name FROM mpa LEFT JOIN MPA_FILMS MF ON mpa.id = mf.mpa_id WHERE film_id = ?";
        return jdbcTemplate.queryForObject(mpaSql, dbMpaStorage::makeMpa, filmId);
    }
}