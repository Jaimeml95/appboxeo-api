package com.boxing.api.service.implementation;

import com.boxing.api.exception.ResourceAlreadyExistsException;
import com.boxing.api.model.Usuario;
import com.boxing.api.repository.UsuarioRepository;
import com.boxing.api.service.UsuarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyección por constructor: La mejor práctica en Spring moderno
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {
        // Regla de negocio profesional: Validar duplicados antes de insertar
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new ResourceAlreadyExistsException("El email '" + usuario.getEmail() + "' ya se encuentra registrado");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }
}