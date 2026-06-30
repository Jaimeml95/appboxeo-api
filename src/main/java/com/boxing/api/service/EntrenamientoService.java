package com.boxing.api.service;

import com.boxing.api.controller.dto.EjercicioRequestDTO;
import com.boxing.api.controller.dto.EjercicioResponseDTO;
import com.boxing.api.controller.dto.EntrenamientoRequestDTO;
import com.boxing.api.controller.dto.EntrenamientoResponseDTO;

import java.util.List;

public interface EntrenamientoService {

    List<EntrenamientoResponseDTO> obtenerTodos();

    EntrenamientoResponseDTO obtenerPorId(Long id);

    EntrenamientoResponseDTO crear(EntrenamientoRequestDTO dto);

    EntrenamientoResponseDTO actualizar(Long id, EntrenamientoRequestDTO dto);

    void eliminar(Long id);

    EjercicioResponseDTO agregarEjercicio(Long entrenamientoId, EjercicioRequestDTO dto);

    EjercicioResponseDTO actualizarEjercicio(Long entrenamientoId, Long ejercicioId, EjercicioRequestDTO dto);

    void eliminarEjercicio(Long entrenamientoId, Long ejercicioId);
}
