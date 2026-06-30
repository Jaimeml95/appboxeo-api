package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;
import com.boxing.api.model.CategoriaVideo;
import com.boxing.api.model.TipoVideo;
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

    private VideoResponseDTO responseDTO;
    private VideoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new VideoResponseDTO(1L, "Jab cruzado", "Técnica básica", TipoVideo.YOUTUBE, "https://youtube.com/watch?v=abc", CategoriaVideo.TECNICA);

        requestDTO = new VideoRequestDTO();
        requestDTO.setTitulo("Jab cruzado");
        requestDTO.setDescripcion("Técnica básica");
        requestDTO.setTipo(TipoVideo.YOUTUBE);
        requestDTO.setUrl("https://youtube.com/watch?v=abc");
        requestDTO.setCategoria(CategoriaVideo.TECNICA);
    }

    @Test
    void listar_devuelveListaYStatus200() throws Exception {
        when(videoService.obtenerTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/videos").with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Jab cruzado"))
                .andExpect(jsonPath("$[0].categoria").value("TECNICA"));
    }

    @Test
    void obtener_idExistente_devuelveVideoYStatus200() throws Exception {
        when(videoService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/videos/1").with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Jab cruzado"));
    }

    @Test
    void obtener_idNoExistente_devuelveStatus404() throws Exception {
        when(videoService.obtenerPorId(99L)).thenThrow(new NoSuchElementException("Video no encontrado"));

        mockMvc.perform(get("/api/v1/videos/99").with(user("test").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_sinRolAdmin_devuelveStatus403() throws Exception {
        mockMvc.perform(post("/api/v1/videos")
                        .with(user("test").roles("BOXEADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void crear_bodyInvalido_devuelveStatus400() throws Exception {
        VideoRequestDTO invalido = new VideoRequestDTO();

        mockMvc.perform(post("/api/v1/videos")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_valido_devuelveVideoYStatus201() throws Exception {
        when(videoService.crear(any(VideoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/videos")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Jab cruzado"));
    }

    @Test
    void eliminar_valido_devuelveStatus204() throws Exception {
        doNothing().when(videoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/videos/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_idNoExistente_devuelveStatus404() throws Exception {
        doThrow(new NoSuchElementException("Video no encontrado")).when(videoService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/videos/99")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
