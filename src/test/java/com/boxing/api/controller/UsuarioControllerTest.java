package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.UsuarioActualizarDTO;
import com.boxing.api.controller.dto.UsuarioAdminCrearDTO;
import com.boxing.api.controller.dto.UsuarioResponseDTO;
import com.boxing.api.model.Rol;
import com.boxing.api.service.JwtService;
import com.boxing.api.service.UsuarioService;
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

@WebMvcTest(UsuarioController.class)
@Import(SecurityConfig.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtService jwtService;

    private UsuarioResponseDTO responseDTO;
    private UsuarioAdminCrearDTO crearDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new UsuarioResponseDTO(1L, "Boxeador Test", "boxeador@example.com", Rol.BOXEADOR, LocalDateTime.now());
        crearDTO = new UsuarioAdminCrearDTO("Boxeador Test", "boxeador@example.com", "password123", Rol.BOXEADOR);
    }

    @Test
    void listar_sinRolAdmin_devuelveStatus403() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios").with(user("test").roles("BOXEADOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listar_conRolAdmin_devuelveListaYStatus200() throws Exception {
        when(usuarioService.obtenerTodos()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/usuarios").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("boxeador@example.com"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void obtener_idExistente_devuelveUsuarioYStatus200() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/usuarios/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("boxeador@example.com"));
    }

    @Test
    void obtener_idNoExistente_devuelveStatus404() throws Exception {
        when(usuarioService.obtenerPorId(99L)).thenThrow(new NoSuchElementException("Usuario no encontrado"));

        mockMvc.perform(get("/api/v1/usuarios/99").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearUsuario_bodyInvalido_devuelveStatus400() throws Exception {
        UsuarioAdminCrearDTO invalido = new UsuarioAdminCrearDTO("", "no-es-email", "123", null);

        mockMvc.perform(post("/api/v1/usuarios")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearUsuario_valido_devuelveUsuarioYStatus201() throws Exception {
        when(usuarioService.crearUsuarioPorAdmin(any(UsuarioAdminCrearDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/usuarios")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("boxeador@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void actualizar_valido_devuelveUsuarioYStatus200() throws Exception {
        UsuarioActualizarDTO actualizarDTO = new UsuarioActualizarDTO("Nombre Actualizado", Rol.ADMIN);
        UsuarioResponseDTO actualizado = new UsuarioResponseDTO(1L, "Nombre Actualizado", "boxeador@example.com", Rol.ADMIN, LocalDateTime.now());
        when(usuarioService.actualizar(eq(1L), any(UsuarioActualizarDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre Actualizado"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void actualizar_idNoExistente_devuelveStatus404() throws Exception {
        UsuarioActualizarDTO actualizarDTO = new UsuarioActualizarDTO("Nombre Actualizado", Rol.ADMIN);
        when(usuarioService.actualizar(eq(99L), any(UsuarioActualizarDTO.class)))
                .thenThrow(new NoSuchElementException("Usuario no encontrado"));

        mockMvc.perform(put("/api/v1/usuarios/99")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizarDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_valido_devuelveStatus204() throws Exception {
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/v1/usuarios/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_idNoExistente_devuelveStatus404() throws Exception {
        doThrow(new NoSuchElementException("Usuario no encontrado")).when(usuarioService).eliminar(99L);

        mockMvc.perform(delete("/api/v1/usuarios/99")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
