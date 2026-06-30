package com.boxing.api.service;

import com.boxing.api.controller.dto.ConfiguracionCronometroRequestDTO;
import com.boxing.api.controller.dto.ConfiguracionCronometroResponseDTO;
import com.boxing.api.model.ConfiguracionCronometro;
import com.boxing.api.model.Rol;
import com.boxing.api.model.Usuario;
import com.boxing.api.repository.ConfiguracionCronometroRepository;
import com.boxing.api.repository.UsuarioRepository;
import com.boxing.api.service.implementation.ConfiguracionCronometroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfiguracionCronometroServiceImplTest {

    @Mock
    private ConfiguracionCronometroRepository cronometroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ConfiguracionCronometroServiceImpl cronometroService;

    private Usuario usuario;
    private ConfiguracionCronometro config;
    private ConfiguracionCronometroRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Carlos", "carlos@boxing.com", "password123", Rol.BOXEADOR);
        usuario.setId(1L);

        config = new ConfiguracionCronometro("Rondas clásicas", 3, 180, 60, usuario);
        config.setId(1L);

        requestDTO = new ConfiguracionCronometroRequestDTO();
        requestDTO.setNombre("Rondas clásicas");
        requestDTO.setRondas(3);
        requestDTO.setDuracionRonda(180);
        requestDTO.setDescanso(60);
    }

    @Test
    void obtenerPorUsuario_devuelveConfiguracionesDelUsuario() {
        when(cronometroRepository.findByUsuarioId(1L)).thenReturn(List.of(config));

        List<ConfiguracionCronometroResponseDTO> resultado = cronometroService.obtenerPorUsuario(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Rondas clásicas");
        assertThat(resultado.get(0).getRondas()).isEqualTo(3);
    }

    @Test
    void obtenerPorUsuario_sinConfiguraciones_devuelveListaVacia() {
        when(cronometroRepository.findByUsuarioId(1L)).thenReturn(List.of());

        List<ConfiguracionCronometroResponseDTO> resultado = cronometroService.obtenerPorUsuario(1L);

        assertThat(resultado).isEmpty();
    }

    @Test
    void crear_usuarioExistente_guardaYDevuelveConfiguracion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cronometroRepository.save(any(ConfiguracionCronometro.class))).thenReturn(config);

        ConfiguracionCronometroResponseDTO resultado = cronometroService.crear(requestDTO, 1L);

        assertThat(resultado.getNombre()).isEqualTo("Rondas clásicas");
        assertThat(resultado.getDuracionRonda()).isEqualTo(180);
        assertThat(resultado.getDescanso()).isEqualTo(60);
        verify(cronometroRepository, times(1)).save(any(ConfiguracionCronometro.class));
    }

    @Test
    void crear_usuarioNoExistente_lanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cronometroService.crear(requestDTO, 99L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        ConfiguracionCronometroRequestDTO dtoActualizado = new ConfiguracionCronometroRequestDTO();
        dtoActualizado.setNombre("Rondas intensas");
        dtoActualizado.setRondas(6);
        dtoActualizado.setDuracionRonda(120);
        dtoActualizado.setDescanso(30);

        when(cronometroRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(config));
        when(cronometroRepository.save(any(ConfiguracionCronometro.class))).thenAnswer(inv -> inv.getArgument(0));

        ConfiguracionCronometroResponseDTO resultado = cronometroService.actualizar(1L, dtoActualizado, 1L);

        assertThat(resultado.getNombre()).isEqualTo("Rondas intensas");
        assertThat(resultado.getRondas()).isEqualTo(6);
        assertThat(resultado.getDuracionRonda()).isEqualTo(120);
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        when(cronometroRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cronometroService.actualizar(99L, requestDTO, 1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Configuración no encontrada");
    }

    @Test
    void eliminar_existente_eliminaConfiguracion() {
        when(cronometroRepository.findByIdAndUsuarioId(1L, 1L)).thenReturn(Optional.of(config));

        cronometroService.eliminar(1L, 1L);

        verify(cronometroRepository, times(1)).delete(config);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(cronometroRepository.findByIdAndUsuarioId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cronometroService.eliminar(99L, 1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Configuración no encontrada");
    }
}
