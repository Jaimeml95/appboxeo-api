package com.boxing.api.service;

import com.boxing.api.controller.dto.UsuarioActualizarDTO;
import com.boxing.api.controller.dto.UsuarioAdminCrearDTO;
import com.boxing.api.controller.dto.UsuarioRegistroDTO;
import com.boxing.api.controller.dto.UsuarioResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsuarioService extends UserDetailsService {

    UsuarioResponseDTO registrarBoxeador(UsuarioRegistroDTO dto);

    UsuarioResponseDTO crearUsuarioPorAdmin(UsuarioAdminCrearDTO dto);

    List<UsuarioResponseDTO> obtenerTodos();

    UsuarioResponseDTO obtenerPorId(Long id);

    UsuarioResponseDTO actualizar(Long id, UsuarioActualizarDTO dto);

    void eliminar(Long id);
}
