package com.boxing.api.integration;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class VideoAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String tokenAdmin;
    private String tokenBoxeador;

    @BeforeEach
    void obtenerTokens() throws Exception {
        tokenAdmin = login("admin@test.com", "adminpass123");

        // Registrar boxeador si no existe y obtener su token
        Map<String, String> registro = Map.of(
                "nombre", "Boxeador Access",
                "email", "boxeador.access@example.com",
                "password", "password123"
        );
        mockMvc.perform(post("/api/v1/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)));

        tokenBoxeador = login("boxeador.access@example.com", "password123");
    }

    @Test
    void getVideos_sinToken_devuelve403() throws Exception {
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getVideos_conTokenBoxeador_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/videos")
                        .header("Authorization", "Bearer " + tokenBoxeador))
                .andExpect(status().isOk());
    }

    @Test
    void getVideos_conTokenAdmin_devuelve200() throws Exception {
        mockMvc.perform(get("/api/v1/videos")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());
    }

    @Test
    void postVideo_conTokenBoxeador_devuelve403() throws Exception {
        Map<String, String> video = Map.of(
                "titulo", "Video prohibido",
                "descripcion", "No debería crearse",
                "tipo", "YOUTUBE",
                "url", "https://youtube.com/watch?v=test",
                "categoria", "TECNICA"
        );

        mockMvc.perform(post("/api/v1/videos")
                        .header("Authorization", "Bearer " + tokenBoxeador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(video)))
                .andExpect(status().isForbidden());
    }

    @Test
    void postVideo_conTokenAdmin_devuelve201() throws Exception {
        Map<String, String> video = Map.of(
                "titulo", "Video Admin",
                "descripcion", "Creado por admin",
                "tipo", "YOUTUBE",
                "url", "https://youtube.com/watch?v=admin",
                "categoria", "TECNICA"
        );

        mockMvc.perform(post("/api/v1/videos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(video)))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password) throws Exception {
        Map<String, String> body = Map.of("email", email, "password", password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }
}
