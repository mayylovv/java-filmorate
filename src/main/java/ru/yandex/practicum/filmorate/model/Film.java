package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import javax.validation.constraints.*;

@Data
public class Film {
    private int id;

    @NotNull(message = "Название не может отсутствовать")
    @NotEmpty(message = "Не может быть пустым название")
    private final String name;

    @NotBlank(message = "Описание фильма не может быть пустым")
    @Size(min = 1, max = 200, message = "Максимальная длина описания — 200 символов")
    private final String description;

    private final LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final int duration;
}