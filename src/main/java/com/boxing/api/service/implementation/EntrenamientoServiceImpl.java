package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.EjercicioRequestDTO;
import com.boxing.api.controller.dto.EjercicioResponseDTO;
import com.boxing.api.controller.dto.EntrenamientoRequestDTO;
import com.boxing.api.controller.dto.EntrenamientoResponseDTO;
import com.boxing.api.model.Ejercicio;
import com.boxing.api.model.Entrenamiento;
import com.boxing.api.repository.EjercicioRepository;
import com.boxing.api.repository.EntrenamientoRepository;
import com.boxing.api.service.EntrenamientoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EntrenamientoServiceImpl implements EntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EjercicioRepository ejercicioRepository;

    public EntrenamientoServiceImpl(EntrenamientoRepository entrenamientoRepository,
                                    EjercicioRepository ejercicioRepository) {
        this.entrenamientoRepository = entrenamientoRepository;
        this.ejercicioRepository = ejercicioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntrenamientoResponseDTO> obtenerTodos() {
        return entrenamientoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EntrenamientoResponseDTO obtenerPorId(Long id) {
        return toResponse(findEntrenamientoById(id));
    }

    @Override
    @Transactional
    public EntrenamientoResponseDTO crear(EntrenamientoRequestDTO dto) {
        Entrenamiento entrenamiento = new Entrenamiento(
                dto.getNombre(), dto.getDescripcion(), dto.getDificultad(), dto.getDuracionEstimada()
        );
        return toResponse(entrenamientoRepository.save(entrenamiento));
    }

    @Override
    @Transactional
    public EntrenamientoResponseDTO actualizar(Long id, EntrenamientoRequestDTO dto) {
        Entrenamiento entrenamiento = findEntrenamientoById(id);
        entrenamiento.setNombre(dto.getNombre());
        entrenamiento.setDescripcion(dto.getDescripcion());
        entrenamiento.setDificultad(dto.getDificultad());
        entrenamiento.setDuracionEstimada(dto.getDuracionEstimada());
        return toResponse(entrenamientoRepository.save(entrenamiento));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        entrenamientoRepository.delete(findEntrenamientoById(id));
    }

    @Override
    @Transactional
    public EjercicioResponseDTO agregarEjercicio(Long entrenamientoId, EjercicioRequestDTO dto) {
        Entrenamiento entrenamiento = findEntrenamientoById(entrenamientoId);
        Ejercicio ejercicio = new Ejercicio(
                dto.getNombre(), dto.getDescripcion(), dto.getSeries(), dto.getRepeticiones(), dto.getDescanso(), entrenamiento
        );
        return toEjercicioResponse(ejercicioRepository.save(ejercicio));
    }

    @Override
    @Transactional
    public EjercicioResponseDTO actualizarEjercicio(Long entrenamientoId, Long ejercicioId, EjercicioRequestDTO dto) {
        Ejercicio ejercicio = ejercicioRepository.findByIdAndEntrenamientoId(ejercicioId, entrenamientoId)
                .orElseThrow(() -> new NoSuchElementException("Ejercicio no encontrado"));
        ejercicio.setNombre(dto.getNombre());
        ejercicio.setDescripcion(dto.getDescripcion());
        ejercicio.setSeries(dto.getSeries());
        ejercicio.setRepeticiones(dto.getRepeticiones());
        ejercicio.setDescanso(dto.getDescanso());
        return toEjercicioResponse(ejercicioRepository.save(ejercicio));
    }

    @Override
    @Transactional
    public void eliminarEjercicio(Long entrenamientoId, Long ejercicioId) {
        Ejercicio ejercicio = ejercicioRepository.findByIdAndEntrenamientoId(ejercicioId, entrenamientoId)
                .orElseThrow(() -> new NoSuchElementException("Ejercicio no encontrado"));
        ejercicioRepository.delete(ejercicio);
    }

    private Entrenamiento findEntrenamientoById(Long id) {
        return entrenamientoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Entrenamiento no encontrado"));
    }

    private EntrenamientoResponseDTO toResponse(Entrenamiento e) {
        List<EjercicioResponseDTO> ejercicios = e.getEjercicios()
                .stream()
                .map(this::toEjercicioResponse)
                .toList();
        return new EntrenamientoResponseDTO(e.getId(), e.getNombre(), e.getDescripcion(), e.getDificultad(), e.getDuracionEstimada(), ejercicios);
    }

    private EjercicioResponseDTO toEjercicioResponse(Ejercicio ej) {
        return new EjercicioResponseDTO(ej.getId(), ej.getNombre(), ej.getDescripcion(), ej.getSeries(), ej.getRepeticiones(), ej.getDescanso());
    }
}
