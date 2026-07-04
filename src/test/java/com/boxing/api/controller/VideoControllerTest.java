package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;
import com.boxing.api.model.VideoCategory;
import com.boxing.api.model.VideoType;
import com.boxing.api.service.JwtService;
import com.boxing.api.service.VideoService;
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

@WebMvcTest(VideoController.class)
@Import(SecurityConfig.class)
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private VideoService videoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private static final UUID VIDEO_ID = UUID.randomUUID();
    private static final UUID NON_EXISTING_ID = UUID.randomUUID();

    private VideoResponseDTO responseDTO;
    private VideoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new VideoResponseDTO(VIDEO_ID, "Jab Cross", "Basic technique", VideoType.YOUTUBE, "https://youtube.com/watch?v=abc", VideoCategory.TECHNIQUE);

        requestDTO = new VideoRequestDTO();
        requestDTO.setTitle("Jab Cross");
        requestDTO.setDescription("Basic technique");
        requestDTO.setType(VideoType.YOUTUBE);
        requestDTO.setUrl("https://youtube.com/watch?v=abc");
        requestDTO.setCategory(VideoCategory.TECHNIQUE);
    }

    @Test
    void list_returnsListAndStatus200() throws Exception {
        when(videoService.getAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/videos").with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Jab Cross"))
                .andExpect(jsonPath("$[0].category").value("TECHNIQUE"));
    }

    @Test
    void get_existingId_returnsVideoAndStatus200() throws Exception {
        when(videoService.getById(VIDEO_ID)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/videos/" + VIDEO_ID).with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VIDEO_ID.toString()))
                .andExpect(jsonPath("$.title").value("Jab Cross"));
    }

    @Test
    void get_nonExistingId_returnsStatus404() throws Exception {
        when(videoService.getById(NON_EXISTING_ID)).thenThrow(new NoSuchElementException("Video not found"));

        mockMvc.perform(get("/api/v1/videos/" + NON_EXISTING_ID).with(user("test").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_withoutAdminRole_returnsStatus403() throws Exception {
        mockMvc.perform(post("/api/v1/videos")
                        .with(user("test").roles("BOXER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_invalidBody_returnsStatus400() throws Exception {
        VideoRequestDTO invalid = new VideoRequestDTO();

        mockMvc.perform(post("/api/v1/videos")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_valid_returnsVideoAndStatus201() throws Exception {
        when(videoService.create(any(VideoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/videos")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Jab Cross"));
    }

    @Test
    void delete_valid_returnsStatus204() throws Exception {
        doNothing().when(videoService).delete(VIDEO_ID);

        mockMvc.perform(delete("/api/v1/videos/" + VIDEO_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExistingId_returnsStatus404() throws Exception {
        doThrow(new NoSuchElementException("Video not found")).when(videoService).delete(NON_EXISTING_ID);

        mockMvc.perform(delete("/api/v1/videos/" + NON_EXISTING_ID)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
