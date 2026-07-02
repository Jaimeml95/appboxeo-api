package com.boxing.api.service;

import com.boxing.api.controller.dto.UsuarioActualizarDTO;
import com.boxing.api.controller.dto.UsuarioAdminCrearDTO;
import com.boxing.api.controller.dto.UsuarioRegistroDTO;
import com.boxing.api.controller.dto.UsuarioResponseDTO;
import com.boxing.api.exception.ResourceAlreadyExistsException;
import com.boxing.api.model.Rol;
import com.boxing.api.model.Usuario;
import com.boxing.api.repository.UsuarioRepository;
import com.boxing.api.service.implementation.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Boxeador Test", "boxeador@example.com", "hashDeContraseña", Rol.BOXEADOR);
        usuario.setId(1L);
    }

    @Test
    void registrarBoxeador_emailNuevo_creaUsuarioConRolBoxeador() {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO("Boxeador Test", "boxeador@example.com", "password123");
        when(usuarioRepository.existsByEmail("boxeador@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashDeContraseña");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioResponseDTO resultado = usuarioService.registrarBoxeador(dto);

        assertThat(resultado.getEmail()).isEqualTo("boxeador@example.com");
        assertThat(resultado.getRol()).isEqualTo(Rol.BOXEADOR);
    }

    @Test
    void registrarBoxeador_emailYaExiste_lanzaExcepcion() {
        UsuarioRegistroDTO dto = new UsuarioRegistroDTO("Boxeador Test", "boxeador@example.com", "password123");
        when(usuarioRepository.existsByEmail("boxeador@example.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrarBoxeador(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void crearUsuarioPorAdmin_emailNuevo_creaUsuarioConRolIndicado() {
        UsuarioAdminCrearDTO dto = new UsuarioAdminCrearDTO("Admin Nuevo", "admin2@example.com", "password123", Rol.ADMIN);
        Usuario admin = new Usuario("Admin Nuevo", "admin2@example.com", "hash", Rol.ADMIN);
        admin.setId(2L);

        when(usuarioRepository.existsByEmail("admin2@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(admin);

        UsuarioResponseDTO resultado = usuarioService.crearUsuarioPorAdmin(dto);

        assertThat(resultado.getRol()).isEqualTo(Rol.ADMIN);
        assertThat(resultado.getEmail()).isEqualTo("admin2@example.com");
    }

    @Test
    void crearUsuarioPorAdmin_emailYaExiste_lanzaExcepcion() {
        UsuarioAdminCrearDTO dto = new UsuarioAdminCrearDTO("Admin Nuevo", "boxeador@example.com", "password123", Rol.ADMIN);
        when(usuarioRepository.existsByEmail("boxeador@example.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuarioPorAdmin(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void obtenerTodos_devuelveListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.obtenerTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("boxeador@example.com");
    }

    @Test
    void obtenerPorId_existente_devuelveUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO resultado = usuarioService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Boxeador Test");
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerPorId(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void actualizar_existente_actualizaNombreYRol() {
        UsuarioActualizarDTO dto = new UsuarioActualizarDTO("Nombre Actualizado", Rol.ADMIN);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO resultado = usuarioService.actualizar(1L, dto);

        assertThat(resultado.getNombre()).isEqualTo("Nombre Actualizado");
        assertThat(resultado.getRol()).isEqualTo(Rol.ADMIN);
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        UsuarioActualizarDTO dto = new UsuarioActualizarDTO("Nombre Actualizado", Rol.ADMIN);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.actualizar(99L, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void eliminar_existente_eliminaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminar(1L);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.eliminar(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void loadUserByUsername_existente_devuelveUserDetails() {
        when(usuarioRepository.findByEmail("boxeador@example.com")).thenReturn(Optional.of(usuario));

        UserDetails resultado = usuarioService.loadUserByUsername("boxeador@example.com");

        assertThat(resultado.getUsername()).isEqualTo("boxeador@example.com");
    }

    @Test
    void loadUserByUsername_noExistente_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.loadUserByUsername("noexiste@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
