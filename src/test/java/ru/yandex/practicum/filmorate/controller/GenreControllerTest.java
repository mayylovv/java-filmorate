package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
public class GenreControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    public GenreControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getGenreTest() throws Exception {
        mockMvc.perform(get("/genres/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Мультфильм"));
    }

    @Test
    void getAllGenresTest() throws Exception {
        String jsonForCheck = "[{\"id\":1,\"name\":\"Комедия\"}," +
                "{\"id\":2,\"name\":\"Драма\"}," +
                "{\"id\":3,\"name\":\"Мультфильм\"}," +
                "{\"id\":4,\"name\":\"Триллер\"}," +
                "{\"id\":5,\"name\":\"Документальный\"}," +
                "{\"id\":6,\"name\":\"Боевик\"}]";
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(jsonForCheck,
                        result.getResponse().getContentAsString(StandardCharsets.UTF_8)));
    }
}