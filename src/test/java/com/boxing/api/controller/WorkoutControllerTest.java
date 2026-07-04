package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.WorkoutRequestDTO;
import com.boxing.api.controller.dto.WorkoutResponseDTO;
import com.boxing.api.model.Difficulty;
import com.boxing.api.service.WorkoutService;
import com.boxing.api.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutController.class)
@Import(SecurityConfig.class)
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static final UUID WORKOUT_ID = UUID.randomUUID();
    private static final UUID NON_EXISTING_ID = UUID.randomUUID();

    private WorkoutResponseDTO responseDTO;
    private WorkoutRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new WorkoutResponseDTO(WORKOUT_ID, "Basic Boxing", "Beginner introduction workout", Difficulty.BEGINNER, 45, List.of());

        requestDTO = new WorkoutRequestDTO();
        requestDTO.setName("Basic Boxing");
        requestDTO.setDescription("Beginner introduction workout");
        requestDTO.setDifficulty(Difficulty.BEGINNER);
        requestDTO.setEstimatedDuration(45);
    }

    @Test
    void list_returnsListAndStatus200() throws Exception {
        when(workoutService.getAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/workouts").with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Basic Boxing"))
                .andExpect(jsonPath("$[0].difficulty").value("BEGINNER"));
    }

    @Test
    void get_existingId_returnsWorkoutAndStatus200() throws Exception {
        when(workoutService.getById(WORKOUT_ID)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/workouts/" + WORKOUT_ID).with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(WORKOUT_ID.toString()))
                .andExpect(jsonPath("$.name").value("Basic Boxing"));
    }

    @Test
    void get_nonExistingId_returnsStatus404() throws Exception {
        when(workoutService.getById(NON_EXISTING_ID)).thenThrow(new NoSuchElementException("Workout not found"));

        mockMvc.perform(get("/api/v1/workouts/" + NON_EXISTING_ID).with(user("test").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withoutAdminRole_returnsStatus403() throws Exception {
        mockMvc.perform(post("/api/v1/workouts")
                        .with(user("test").roles("BOXER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_invalidBody_returnsStatus400() throws Exception {
        WorkoutRequestDTO invalid = new WorkoutRequestDTO();

        mockMvc.perform(post("/api/v1/workouts")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_valid_returnsWorkoutAndStatus201() throws Exception {
        when(workoutService.create(any(WorkoutRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/workouts")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Basic Boxing"));
    }

    @Test
    void delete_valid_returnsStatus204() throws Exception {
        doNothing().when(workoutService).delete(WORKOUT_ID);

        mockMvc.perform(delete("/api/v1/workouts/" + WORKOUT_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExistingId_returnsStatus404() throws Exception {
        doThrow(new NoSuchElementException("Workout not found")).when(workoutService).delete(NON_EXISTING_ID);

        mockMvc.perform(delete("/api/v1/workouts/" + NON_EXISTING_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
