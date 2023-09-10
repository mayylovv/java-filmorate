package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import javax.validation.constraints.*;


@Data
public class User {
    private int id;
    @Email(message = "В адресе электронной почты ошибка")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @NotEmpty(message = "Логин не может быть пустым")
    private final String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
}
