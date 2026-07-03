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

    private String adminToken;

    @BeforeEach
    void getAdminToken() throws Exception {
        Map<String, String> body = Map.of("email", "admin@test.com", "password", "adminpass123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        adminToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    void fullCrud_video() throws Exception {
        // CREATE
        Map<String, String> newVideo = Map.of(
                "title", "Direct Jab",
                "description", "Basic jab technique",
                "type", "YOUTUBE",
                "url", "https://youtube.com/watch?v=jab123",
                "category", "TECHNIQUE"
        );

        MvcResult createResult = mockMvc.perform(post("/api/v1/videos")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVideo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Direct Jab"))
                .andExpect(jsonPath("$.category").value("TECHNIQUE"))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        // READ (list)
        mockMvc.perform(get("/api/v1/videos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        // READ (by id)
        mockMvc.perform(get("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Direct Jab"));

        // UPDATE
        Map<String, String> updatedVideo = Map.of(
                "title", "Direct Jab - updated",
                "description", "Basic jab technique, improved version",
                "type", "YOUTUBE",
                "url", "https://youtube.com/watch?v=jab123",
                "category", "TECHNIQUE"
        );

        mockMvc.perform(put("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVideo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Direct Jab - updated"));

        // DELETE
        mockMvc.perform(delete("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify it no longer exists
        mockMvc.perform(get("/api/v1/videos/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
