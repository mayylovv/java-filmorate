package ru.yandex.practicum.filmorate.storage.dbstorage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
public class DBMpaStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("id"),
                resultSet.getString("name"));
    }

    @Override
    public Optional<Mpa> getMpa(int id) {
        String sqlMpa = "SELECT * FROM mpa WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlMpa, this::makeMpa, id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, this::makeMpa);
    }
}