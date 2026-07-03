package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserAdminCreateDTO;
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

    private UserResponseDTO responseDTO;
    private UserAdminCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new UserResponseDTO(1L, "Test Boxer", "boxer@example.com", Role.BOXER, LocalDateTime.now());
        createDTO = new UserAdminCreateDTO("Test Boxer", "boxer@example.com", "password123", Role.BOXER);
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
        when(userService.getById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/users/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("boxer@example.com"));
    }

    @Test
    void get_nonExistingId_returnsStatus404() throws Exception {
        when(userService.getById(99L)).thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(get("/api/v1/users/99").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_invalidBody_returnsStatus400() throws Exception {
        UserAdminCreateDTO invalid = new UserAdminCreateDTO("", "not-an-email", "123", null);

        mockMvc.perform(post("/api/v1/users")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_valid_returnsUserAndStatus201() throws Exception {
        when(userService.createUser(any(UserAdminCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/users")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("boxer@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void update_valid_returnsUserAndStatus200() throws Exception {
        UserUpdateDTO updateDTO = new UserUpdateDTO("Updated Name", Role.ADMIN);
        UserResponseDTO updated = new UserResponseDTO(1L, "Updated Name", "boxer@example.com", Role.ADMIN, LocalDateTime.now());
        when(userService.update(eq(1L), any(UserUpdateDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/users/1")
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
        when(userService.update(eq(99L), any(UserUpdateDTO.class)))
                .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put("/api/v1/users/99")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_valid_returnsStatus204() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/v1/users/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExistingId_returnsStatus404() throws Exception {
        doThrow(new NoSuchElementException("User not found")).when(userService).delete(99L);

        mockMvc.perform(delete("/api/v1/users/99")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
