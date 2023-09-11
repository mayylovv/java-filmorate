package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Film {
    @Getter
    @Setter
    private int id;

    @NotNull(message = "Название не может отсутствовать")
    @NotEmpty(message = "Не может быть пустым название")
    @Getter
    private String name;

    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    @Getter
    private String description;

    @Getter
    @Setter
    private LocalDate releaseDate;


    @Positive(message = "Продолжительность фильма должна быть положительной")
    @Getter
    private int duration;

    @Getter
    private Set<Integer> usersLikes = new HashSet<>();
}