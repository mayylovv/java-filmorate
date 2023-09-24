package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Getter
    @Setter
    int id;

    @Email(message = "В адресе электронной почты ошибка")
    @Getter
    String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @NotEmpty(message = "Логин не может быть пустым")
    @Getter
    String login;

    @Getter
    @Setter
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @Getter
    LocalDate birthday;

    @Getter
    Set<Integer> friends = new HashSet<>();
}

