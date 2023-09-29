package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class ErrorResponse {
    String description;
    String error;

    public ErrorResponse(String description, String error) {
        this.description = description;
        this.error = error;
    }
}