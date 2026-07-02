package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.UsuarioActualizarDTO;
import com.boxing.api.controller.dto.UsuarioAdminCrearDTO;
import com.boxing.api.controller.dto.UsuarioRegistroDTO;
import com.boxing.api.controller.dto.UsuarioResponseDTO;
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
import java.util.NoSuchElementException;

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
    public UsuarioResponseDTO registrarBoxeador(UsuarioRegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("El email '" + dto.getEmail() + "' ya se encuentra registrado");
        }
        Usuario usuario = new Usuario(dto.getNombre(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()), Rol.BOXEADOR);
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crearUsuarioPorAdmin(UsuarioAdminCrearDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("El email '" + dto.email() + "' ya se encuentra registrado");
        }
        Usuario usuario = new Usuario(dto.nombre(), dto.email(), passwordEncoder.encode(dto.password()), dto.rol());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        return toResponse(findUsuarioById(id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioActualizarDTO dto) {
        Usuario usuario = findUsuarioById(id);
        usuario.setNombre(dto.nombre());
        usuario.setRol(dto.rol());
        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(findUsuarioById(id));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
    }

    private Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    private UsuarioResponseDTO toResponse(Usuario u) {
        return new UsuarioResponseDTO(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.getFechaCreacion());
    }
}
