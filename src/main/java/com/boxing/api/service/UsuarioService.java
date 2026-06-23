package com.boxing.api.service;

import com.boxing.api.model.Usuario;
import java.util.List;

    public interface UsuarioService {
        Usuario registrarUsuario(Usuario usuario);
        List<Usuario> obtenerTodos();
    }
