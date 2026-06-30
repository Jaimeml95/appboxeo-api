package com.boxing.api.service;

import com.boxing.api.controller.dto.EjercicioRequestDTO;
import com.boxing.api.controller.dto.EjercicioResponseDTO;
import com.boxing.api.controller.dto.EntrenamientoRequestDTO;
import com.boxing.api.controller.dto.EntrenamientoResponseDTO;
import com.boxing.api.model.Dificultad;
import com.boxing.api.model.Ejercicio;
import com.boxing.api.model.Entrenamiento;
import com.boxing.api.repository.EjercicioRepository;
import com.boxing.api.repository.EntrenamientoRepository;
import com.boxing.api.service.implementation.EntrenamientoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntrenamientoServiceImplTest {

    @Mock
    private EntrenamientoRepository entrenamientoRepository;

    @Mock
    private EjercicioRepository ejercicioRepository;

    @InjectMocks
    private EntrenamientoServiceImpl entrenamientoService;

    private Entrenamiento entrenamiento;
    private EntrenamientoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        entrenamiento = new Entrenamiento("Cardio básico", "Sesión de cardio", Dificultad.PRINCIPIANTE, 30);
        entrenamiento.setId(1L);

        requestDTO = new EntrenamientoRequestDTO();
        requestDTO.setNombre("Cardio básico");
        requestDTO.setDescripcion("Sesión de cardio");
        requestDTO.setDificultad(Dificultad.PRINCIPIANTE);
        requestDTO.setDuracionEstimada(30);
    }

    @Test
    void obtenerTodos_devuelveListaDeEntrenamientos() {
        when(entrenamientoRepository.findAll()).thenReturn(List.of(entrenamiento));

        List<EntrenamientoResponseDTO> resultado = entrenamientoService.obtenerTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Cardio básico");
        assertThat(resultado.get(0).getDificultad()).isEqualTo(Dificultad.PRINCIPIANTE);
    }

    @Test
    void obtenerTodos_listaVacia_devuelveListaVacia() {
        when(entrenamientoRepository.findAll()).thenReturn(List.of());

        List<EntrenamientoResponseDTO> resultado = entrenamientoService.obtenerTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    void obtenerPorId_existente_devuelveEntrenamiento() {
        when(entrenamientoRepository.findById(1L)).thenReturn(Optional.of(entrenamiento));

        EntrenamientoResponseDTO resultado = entrenamientoService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Cardio básico");
        assertThat(resultado.getDuracionEstimada()).isEqualTo(30);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(entrenamientoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenamientoService.obtenerPorId(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Entrenamiento no encontrado");
    }

    @Test
    void crear_guardaYDevuelveEntrenamiento() {
        when(entrenamientoRepository.save(any(Entrenamiento.class))).thenReturn(entrenamiento);

        EntrenamientoResponseDTO resultado = entrenamientoService.crear(requestDTO);

        assertThat(resultado.getNombre()).isEqualTo("Cardio básico");
        assertThat(resultado.getDificultad()).isEqualTo(Dificultad.PRINCIPIANTE);
        verify(entrenamientoRepository, times(1)).save(any(Entrenamiento.class));
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        EntrenamientoRequestDTO dtoActualizado = new EntrenamientoRequestDTO();
        dtoActualizado.setNombre("Fuerza avanzada");
        dtoActualizado.setDescripcion("Sesión de fuerza");
        dtoActualizado.setDificultad(Dificultad.AVANZADO);
        dtoActualizado.setDuracionEstimada(60);

        when(entrenamientoRepository.findById(1L)).thenReturn(Optional.of(entrenamiento));
        when(entrenamientoRepository.save(any(Entrenamiento.class))).thenAnswer(inv -> inv.getArgument(0));

        EntrenamientoResponseDTO resultado = entrenamientoService.actualizar(1L, dtoActualizado);

        assertThat(resultado.getNombre()).isEqualTo("Fuerza avanzada");
        assertThat(resultado.getDificultad()).isEqualTo(Dificultad.AVANZADO);
        assertThat(resultado.getDuracionEstimada()).isEqualTo(60);
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        when(entrenamientoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenamientoService.actualizar(99L, requestDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Entrenamiento no encontrado");
    }

    @Test
    void eliminar_existente_eliminaEntrenamiento() {
        when(entrenamientoRepository.findById(1L)).thenReturn(Optional.of(entrenamiento));

        entrenamientoService.eliminar(1L);

        verify(entrenamientoRepository, times(1)).delete(entrenamiento);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(entrenamientoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenamientoService.eliminar(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Entrenamiento no encontrado");
    }

    @Test
    void agregarEjercicio_existente_guardaYDevuelveEjercicio() {
        EjercicioRequestDTO ejercicioDTO = new EjercicioRequestDTO();
        ejercicioDTO.setNombre("Sentadillas");
        ejercicioDTO.setDescripcion("Sentadillas con peso");
        ejercicioDTO.setSeries(4);
        ejercicioDTO.setRepeticiones(12);
        ejercicioDTO.setDescanso(60);

        Ejercicio ejercicio = new Ejercicio("Sentadillas", "Sentadillas con peso", 4, 12, 60, entrenamiento);
        ejercicio.setId(1L);

        when(entrenamientoRepository.findById(1L)).thenReturn(Optional.of(entrenamiento));
        when(ejercicioRepository.save(any(Ejercicio.class))).thenReturn(ejercicio);

        EjercicioResponseDTO resultado = entrenamientoService.agregarEjercicio(1L, ejercicioDTO);

        assertThat(resultado.getNombre()).isEqualTo("Sentadillas");
        assertThat(resultado.getSeries()).isEqualTo(4);
        verify(ejercicioRepository, times(1)).save(any(Ejercicio.class));
    }

    @Test
    void agregarEjercicio_entrenamientoNoExistente_lanzaExcepcion() {
        when(entrenamientoRepository.findById(99L)).thenReturn(Optional.empty());

        EjercicioRequestDTO ejercicioDTO = new EjercicioRequestDTO();

        assertThatThrownBy(() -> entrenamientoService.agregarEjercicio(99L, ejercicioDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Entrenamiento no encontrado");
    }

    @Test
    void eliminarEjercicio_noExistente_lanzaExcepcion() {
        when(ejercicioRepository.findByIdAndEntrenamientoId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entrenamientoService.eliminarEjercicio(1L, 99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Ejercicio no encontrado");
    }
}
