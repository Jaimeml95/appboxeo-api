package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.EntrenamientoRequestDTO;
import com.boxing.api.controller.dto.EntrenamientoResponseDTO;
import com.boxing.api.model.Dificultad;
import com.boxing.api.service.EntrenamientoService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EntrenamientoController.class)
@Import(SecurityConfig.class)
class EntrenamientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EntrenamientoService entrenamientoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private EntrenamientoResponseDTO responseDTO;
    private EntrenamientoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new EntrenamientoResponseDTO(1L, "Boxeo básico", "Entrenamiento de iniciación", Dificultad.PRINCIPIANTE, 45, List.of());

        requestDTO = new EntrenamientoRequestDTO();
        requestDTO.setNombre("Boxeo básico");
        requestDTO.setDescripcion("Entrenamiento de iniciación");
        requestDTO.setDificultad(Dificultad.PRINCIPIANTE);
        requestDTO.setDuracionEstimada(45);
    }

    @Test
    void listar_devuelveListaYStatus200() throws Exception {
        when(entrenamientoService.obtenerTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/entrenamientos").with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Boxeo básico"))
                .andExpect(jsonPath("$[0].dificultad").value("PRINCIPIANTE"));
    }

    @Test
    void obtener_idExistente_devuelveEntrenamientoYStatus200() throws Exception {
        when(entrenamientoService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/entrenamientos/1").with(user("test").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Boxeo básico"));
    }

    @Test
    void obtener_idNoExistente_devuelveStatus404() throws Exception {
        when(entrenamientoService.obtenerPorId(99L)).thenThrow(new NoSuchElementException("Entrenamiento no encontrado"));

        mockMvc.perform(get("/api/v1/entrenamientos/99").with(user("test").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void crear_sinRolAdmin_devuelveStatus403() throws Exception {
        mockMvc.perform(post("/api/v1/entrenamientos")
                        .with(user("test").roles("BOXEADOR"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void crear_bodyInvalido_devuelveStatus400() throws Exception {
        EntrenamientoRequestDTO invalido = new EntrenamientoRequestDTO();

        mockMvc.perform(post("/api/v1/entrenamientos")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_valido_devuelveEntrenamientoYStatus201() throws Exception {
        when(entrenamientoService.crear(any(EntrenamientoRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/entrenamientos")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Boxeo básico"));
    }

    @Test
    void eliminar_valido_devuelveStatus204() throws Exception {
        doNothing().when(entrenamientoService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/entrenamientos/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_idNoExistente_devuelveStatus404() throws Exception {
        doThrow(new NoSuchElementException("Entrenamiento no encontrado")).when(entrenamientoService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/entrenamientos/99")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
