package com.boxing.api.integration;

import com.boxing.api.model.Role;
import com.boxing.api.model.User;
import com.boxing.api.repository.UserRepository;
import com.boxing.api.service.JwtService;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String adminToken;
    private String boxerToken;

    @BeforeEach
    void getTokens() throws Exception {
        adminToken = login("admin@test.com", "adminpass123");

        // Boxers only ever exist via Google Sign-In, so a boxer test account is
        // provisioned directly instead of going through an HTTP registration flow.
        // find-or-create because @BeforeEach runs before every test in this class.
        User boxer = userRepository.findByGoogleId("google-sub-access")
                .orElseGet(() -> userRepository.save(
                        User.forGoogleSignIn("Access Boxer", "boxer.access@example.com", "google-sub-access",
                                "https://example.com/access.jpg", Role.BOXER)));
        boxerToken = jwtService.generateToken(boxer);
    }

    @Test
    void getVideos_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getVideos_withBoxerToken_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/videos")
                        .header("Authorization", "Bearer " + boxerToken))
                .andExpect(status().isOk());
    }

    @Test
    void getVideos_withAdminToken_returns200() throws Exception {
        mockMvc.perform(get("/api/v1/videos")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void postVideo_withBoxerToken_returns403() throws Exception {
        Map<String, String> video = Map.of(
                "title", "Forbidden Video",
                "description", "Should not be created",
                "type", "YOUTUBE",
                "url", "https://youtube.com/watch?v=test",
                "category", "TECHNIQUE"
        );

        mockMvc.perform(post("/api/v1/videos")
                        .header("Authorization", "Bearer " + boxerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(video)))
                .andExpect(status().isForbidden());
    }

    @Test
    void postVideo_withAdminToken_returns201() throws Exception {
        Map<String, String> video = Map.of(
                "title", "Admin Video",
                "description", "Created by admin",
                "type", "YOUTUBE",
                "url", "https://youtube.com/watch?v=admin",
                "category", "TECHNIQUE"
        );

        mockMvc.perform(post("/api/v1/videos")
                        .header("Authorization", "Bearer " + adminToken)
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
