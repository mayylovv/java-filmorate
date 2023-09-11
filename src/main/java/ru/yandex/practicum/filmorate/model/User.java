package ru.yandex.practicum.filmorate.model;



import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;


public class User {
    @Getter
    @Setter
    private int id;

    @Email(message = "В адресе электронной почты ошибка")
    @Getter
    private String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @NotEmpty(message = "Логин не может быть пустым")
    @Getter
    private String login;

    @Getter
    @Setter
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @Getter
    private LocalDate birthday;

    @Getter
    private Set<Integer> friends = new HashSet<>();
}

