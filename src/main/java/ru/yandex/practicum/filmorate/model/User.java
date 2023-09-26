package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    int id;

    @Email(message = "В адресе электронной почты ошибка")
    String email;

    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    @NotEmpty(message = "Логин не может быть пустым")
    @Size(min = 1, max = 20)
    String login;

    String name;

    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
}
