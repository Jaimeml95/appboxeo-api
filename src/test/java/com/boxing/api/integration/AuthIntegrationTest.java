package com.boxing.api.integration;

import com.boxing.api.model.Role;
import com.boxing.api.repository.UserRepository;
import com.boxing.api.service.GoogleTokenVerifier;
import com.boxing.api.service.GoogleUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private GoogleTokenVerifier googleTokenVerifier;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void googleLogin_firstTime_createsUserWithBoxerRole_andReturnsJwt() throws Exception {
        when(googleTokenVerifier.verify("valid-token"))
                .thenReturn(new GoogleUserInfo("google-sub-1", "new.boxer@example.com", "New Boxer",
                        "https://example.com/photo1.jpg"));

        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("idToken", "valid-token"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("new.boxer@example.com"))
                .andExpect(jsonPath("$.user.role").value("BOXER"))
                .andExpect(jsonPath("$.user.password").doesNotExist());

        assertThat(userRepository.findByGoogleId("google-sub-1"))
                .isPresent()
                .get()
                .satisfies(u -> assertThat(u.getRole()).isEqualTo(Role.BOXER));
    }

    @Test
    void googleLogin_sameGoogleIdTwice_doesNotCreateDuplicateUser() throws Exception {
        when(googleTokenVerifier.verify("valid-token-2"))
                .thenReturn(new GoogleUserInfo("google-sub-2", "repeat.boxer@example.com", "Repeat Boxer",
                        "https://example.com/photo2.jpg"));

        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("idToken", "valid-token-2"))))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("idToken", "valid-token-2"))))
                .andExpect(status().isOk());

        long matches = userRepository.findAll().stream()
                .filter(u -> "google-sub-2".equals(u.getGoogleId()))
                .count();
        assertThat(matches).isEqualTo(1);
    }

    @Test
    void googleLogin_invalidToken_returns401() throws Exception {
        when(googleTokenVerifier.verify("bad-token"))
                .thenThrow(new BadCredentialsException("Invalid Google token"));

        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("idToken", "bad-token"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void googleLogin_blankIdToken_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("idToken", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void localLogin_withGoogleOnlyAccountEmail_returns401_notServerError() throws Exception {
        when(googleTokenVerifier.verify("provisioning-token"))
                .thenReturn(new GoogleUserInfo("google-sub-3", "google.only@example.com", "Google Only",
                        "https://example.com/photo3.jpg"));
        mockMvc.perform(post("/api/v1/auth/google")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("idToken", "provisioning-token"))))
                .andExpect(status().isOk());

        Map<String, String> body = Map.of(
                "email", "google.only@example.com",
                "password", "whatever123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_withValidCredentials_returns200_andJwt() throws Exception {
        Map<String, String> body = Map.of(
                "email", "admin@test.com",
                "password", "adminpass123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("admin@test.com"))
                .andExpect(jsonPath("$.user.role").value("ADMIN"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void login_withInvalidCredentials_returns401() throws Exception {
        Map<String, String> body = Map.of(
                "email", "admin@test.com",
                "password", "wrong_password"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
