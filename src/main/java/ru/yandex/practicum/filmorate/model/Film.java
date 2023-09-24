package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    @Getter
    @Setter
    int id;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @Getter
    String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    @Getter
    String description;

    @Getter
    @Setter
    LocalDate releaseDate;


    @Positive(message = "Продолжительность фильма должна быть положительной")
    @Getter
    int duration;

    @Getter
    Set<Integer> usersLikes = new HashSet<>();
}