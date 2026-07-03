package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.TimerConfigurationRequestDTO;
import com.boxing.api.controller.dto.TimerConfigurationResponseDTO;
import com.boxing.api.model.Role;
import com.boxing.api.model.User;
import com.boxing.api.service.TimerConfigurationService;
import com.boxing.api.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimerConfigurationController.class)
@Import(SecurityConfig.class)
class TimerConfigurationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TimerConfigurationService timerConfigurationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private TimerConfigurationResponseDTO responseDTO;
    private TimerConfigurationRequestDTO requestDTO;
    private Authentication authBoxer;

    @BeforeEach
    void setUp() {
        responseDTO = new TimerConfigurationResponseDTO(1L, "Classic Sparring", 12, 180, 60);

        requestDTO = new TimerConfigurationRequestDTO();
        requestDTO.setName("Classic Sparring");
        requestDTO.setRounds(12);
        requestDTO.setRoundDuration(180);
        requestDTO.setRest(60);

        User user = new User("Ana Lopez", "ana@boxing.com", "hashed", Role.BOXER);
        user.setId(1L);
        authBoxer = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Test
    void list_authenticated_returnsListAndStatus200() throws Exception {
        when(timerConfigurationService.getByUser(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/timer-configurations")
                        .with(authentication(authBoxer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Classic Sparring"))
                .andExpect(jsonPath("$[0].rounds").value(12));
    }

    @Test
    void list_unauthenticated_returnsStatus403() throws Exception {
        // With Spring Security 6+'s AnonymousAuthenticationToken, access is denied → 403
        mockMvc.perform(get("/api/v1/timer-configurations"))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_valid_returnsConfigurationAndStatus201() throws Exception {
        when(timerConfigurationService.create(any(TimerConfigurationRequestDTO.class), eq(1L))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/timer-configurations")
                        .with(authentication(authBoxer))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Classic Sparring"));
    }

    @Test
    void create_invalidBody_returnsStatus400() throws Exception {
        TimerConfigurationRequestDTO invalid = new TimerConfigurationRequestDTO();

        mockMvc.perform(post("/api/v1/timer-configurations")
                        .with(authentication(authBoxer))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_valid_returnsStatus204() throws Exception {
        doNothing().when(timerConfigurationService).delete(1L, 1L);

        mockMvc.perform(delete("/api/v1/timer-configurations/1")
                        .with(authentication(authBoxer))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExistingId_returnsStatus404() throws Exception {
        doThrow(new NoSuchElementException("Configuration not found"))
                .when(timerConfigurationService).delete(99L, 1L);

        mockMvc.perform(delete("/api/v1/timer-configurations/99")
                        .with(authentication(authBoxer))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
