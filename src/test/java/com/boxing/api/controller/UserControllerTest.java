package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
import com.boxing.api.model.Role;
import com.boxing.api.service.JwtService;
import com.boxing.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NON_EXISTING_ID = UUID.randomUUID();

    private UserResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new UserResponseDTO(USER_ID, "Test Boxer", "boxer@example.com", Role.BOXER, null, LocalDateTime.now());
    }

    @Test
    void list_withoutAdminRole_returnsStatus403() throws Exception {
        mockMvc.perform(get("/api/v1/users").with(user("test").roles("BOXER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void list_withAdminRole_returnsListAndStatus200() throws Exception {
        when(userService.getAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/users").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("boxer@example.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void get_existingId_returnsUserAndStatus200() throws Exception {
        when(userService.getById(USER_ID)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/users/" + USER_ID).with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID.toString()))
                .andExpect(jsonPath("$.email").value("boxer@example.com"));
    }

    @Test
    void get_nonExistingId_returnsStatus404() throws Exception {
        when(userService.getById(NON_EXISTING_ID)).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get("/api/v1/users/" + NON_EXISTING_ID).with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_valid_returnsUserAndStatus200() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", Role.ADMIN);
        UserResponseDTO updated = new UserResponseDTO(USER_ID, "Updated Name", "boxer@example.com", Role.ADMIN, null, LocalDateTime.now());
        when(userService.update(eq(USER_ID), any(UserUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/users/" + USER_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void update_nonExistingId_returnsStatus404() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", Role.ADMIN);
        when(userService.update(eq(NON_EXISTING_ID), any(UserUpdateDTO.class)))
                .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put("/api/v1/users/" + NON_EXISTING_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_valid_returnsStatus204() throws Exception {
        doNothing().when(userService).delete(USER_ID);

        mockMvc.perform(delete("/api/v1/users/" + USER_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExistingId_returnsStatus404() throws Exception {
        doThrow(new NoSuchElementException("User not found")).when(userService).delete(NON_EXISTING_ID);

        mockMvc.perform(delete("/api/v1/users/" + NON_EXISTING_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
