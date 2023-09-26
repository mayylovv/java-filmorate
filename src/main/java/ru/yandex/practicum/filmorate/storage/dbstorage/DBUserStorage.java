package ru.yandex.practicum.filmorate.storage.dbstorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
public class DBUserStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DBUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlUser = "INSERT INTO users (email, login, name, birthday) VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlUser, new String[]{"id"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlUser = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE id = ?";
        jdbcTemplate.update(sqlUser, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getId());
        return user;
    }


    @Override
    public Optional<User> deleteUser(int id) {
        String sqlQuery = "DELETE from USERS where ID = ?";
        Optional<User> user = getUser(id);
        jdbcTemplate.update(sqlQuery, id);
        return user;
    }

    @Override
    public Optional<User> getUser(int id) {
        String sqlUser = "SELECT * from USERS where ID = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlUser, this::makeUser, id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        String sqlUsers = "SELECT * from USERS";
        return jdbcTemplate.query(sqlUsers, this::makeUser);
    }

    @Override
    public List<Integer> addFriend(int idOfPerson1, int idOfPerson2) {
        String sqlForWrite = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES (?, ?)";
        jdbcTemplate.update(sqlForWrite, idOfPerson1, idOfPerson2);
        return List.of(idOfPerson1, idOfPerson2);
    }

    @Override
    public List<Integer> deleteFriend(int idOfPerson1, int idOfPerson2) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, idOfPerson1, idOfPerson2);
        return List.of(idOfPerson1, idOfPerson2);
    }

    @Override
    public List<User> getFriendsListOfPerson(int id) {
        String sql = "SELECT id, email, login, name, birthday FROM USERS " +
                "LEFT JOIN friendship f on users.id = f.friend_id WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::makeUser, id);
    }

    @Override
    public List<User> getListOfCommonFriends(int idOfPerson1, int idOfPerson2) {
        String sql = "SELECT id, email, login, name, birthday FROM friendship AS f " +
                "LEFT JOIN users u ON u.id = f.friend_id WHERE f.user_id = ? AND f.friend_id IN ( " +
                "SELECT friend_id FROM friendship AS f LEFT JOIN users AS u ON u.id = f.friend_id " +
                "WHERE f.user_id = ? )";
        return jdbcTemplate.query(sql, this::makeUser, idOfPerson1, idOfPerson2);
    }

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getDate("birthday").toLocalDate());
    }
}
