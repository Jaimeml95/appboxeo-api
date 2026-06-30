package com.boxing.api.controller;

import com.boxing.api.config.SecurityConfig;
import com.boxing.api.controller.dto.ConfiguracionCronometroRequestDTO;
import com.boxing.api.controller.dto.ConfiguracionCronometroResponseDTO;
import com.boxing.api.model.Rol;
import com.boxing.api.model.Usuario;
import com.boxing.api.service.ConfiguracionCronometroService;
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

@WebMvcTest(ConfiguracionCronometroController.class)
@Import(SecurityConfig.class)
class ConfiguracionCronometroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ConfiguracionCronometroService cronometroService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private ConfiguracionCronometroResponseDTO responseDTO;
    private ConfiguracionCronometroRequestDTO requestDTO;
    private Authentication authBoxeador;

    @BeforeEach
    void setUp() {
        responseDTO = new ConfiguracionCronometroResponseDTO(1L, "Sparring clásico", 12, 180, 60);

        requestDTO = new ConfiguracionCronometroRequestDTO();
        requestDTO.setNombre("Sparring clásico");
        requestDTO.setRondas(12);
        requestDTO.setDuracionRonda(180);
        requestDTO.setDescanso(60);

        Usuario usuario = new Usuario("Ana López", "ana@boxing.com", "hashed", Rol.BOXEADOR);
        usuario.setId(1L);
        authBoxeador = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
    }

    @Test
    void listar_autenticado_devuelveListaYStatus200() throws Exception {
        when(cronometroService.obtenerPorUsuario(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/cronometro/configuraciones")
                        .with(authentication(authBoxeador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Sparring clásico"))
                .andExpect(jsonPath("$[0].rondas").value(12));
    }

    @Test
    void listar_sinAutenticar_devuelveStatus403() throws Exception {
        // En Spring Security 6+ con AnonymousAuthenticationToken, acceso denegado → 403
        mockMvc.perform(get("/api/v1/cronometro/configuraciones"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crear_valido_devuelveConfiguracionYStatus201() throws Exception {
        when(cronometroService.crear(any(ConfiguracionCronometroRequestDTO.class), eq(1L))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/cronometro/configuraciones")
                        .with(authentication(authBoxeador))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Sparring clásico"));
    }

    @Test
    void crear_bodyInvalido_devuelveStatus400() throws Exception {
        ConfiguracionCronometroRequestDTO invalido = new ConfiguracionCronometroRequestDTO();

        mockMvc.perform(post("/api/v1/cronometro/configuraciones")
                        .with(authentication(authBoxeador))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_valido_devuelveStatus204() throws Exception {
        doNothing().when(cronometroService).eliminar(1L, 1L);

        mockMvc.perform(delete("/api/v1/cronometro/configuraciones/1")
                        .with(authentication(authBoxeador))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_idNoExistente_devuelveStatus404() throws Exception {
        doThrow(new NoSuchElementException("Configuración no encontrada"))
                .when(cronometroService).eliminar(99L, 1L);

        mockMvc.perform(delete("/api/v1/cronometro/configuraciones/99")
                        .with(authentication(authBoxeador))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
