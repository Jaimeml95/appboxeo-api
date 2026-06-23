package com.boxing.api.service;

import com.boxing.api.controller.dto.UsuarioAdminCrearDTO;
import com.boxing.api.controller.dto.UsuarioRegistroDTO;
import com.boxing.api.model.Usuario;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UsuarioService extends UserDetailsService {

    Usuario registrarBoxeador(UsuarioRegistroDTO dto);

    Usuario crearUsuarioPorAdmin(UsuarioAdminCrearDTO dto);

    List<Usuario> obtenerTodos();
}
