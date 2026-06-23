package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.UsuarioAdminCrearDTO;
import com.boxing.api.controller.dto.UsuarioRegistroDTO;
import com.boxing.api.exception.ResourceAlreadyExistsException;
import com.boxing.api.model.Rol;
import com.boxing.api.model.Usuario;
import com.boxing.api.repository.UsuarioRepository;
import com.boxing.api.service.UsuarioService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario registrarBoxeador(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("El email '" + dto.getEmail() + "' ya se encuentra registrado");
        }
        Usuario usuario = new Usuario(dto.getNombre(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()), Rol.BOXEADOR);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario crearUsuarioPorAdmin(UsuarioAdminCrearDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("El email '" + dto.email() + "' ya se encuentra registrado");
        }
        Usuario usuario = new Usuario(dto.nombre(), dto.email(), passwordEncoder.encode(dto.password()), dto.rol());
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }
}
