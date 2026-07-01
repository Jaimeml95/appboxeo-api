package com.boxing.api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VideoCrudIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String tokenAdmin;

    @BeforeEach
    void obtenerTokenAdmin() throws Exception {
        Map<String, String> body = Map.of("email", "admin@test.com", "password", "adminpass123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        tokenAdmin = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    void crudCompleto_video() throws Exception {
        // CREATE
        Map<String, String> nuevoVideo = Map.of(
                "titulo", "Jab directo",
                "descripcion", "Técnica básica del jab",
                "tipo", "YOUTUBE",
                "url", "https://youtube.com/watch?v=jab123",
                "categoria", "TECNICA"
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/videos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoVideo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Jab directo"))
                .andExpect(jsonPath("$.categoria").value("TECNICA"))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        // READ (lista)
        mockMvc.perform(get("/api/v1/videos")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        // READ (por id)
        mockMvc.perform(get("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Jab directo"));

        // UPDATE
        Map<String, String> videoActualizado = Map.of(
                "titulo", "Jab directo - actualizado",
                "descripcion", "Técnica básica del jab, versión mejorada",
                "tipo", "YOUTUBE",
                "url", "https://youtube.com/watch?v=jab123",
                "categoria", "TECNICA"
        );

        mockMvc.perform(put("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Jab directo - actualizado"));

        // DELETE
        mockMvc.perform(delete("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());

        // Verificar que ya no existe
        mockMvc.perform(get("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }
}
