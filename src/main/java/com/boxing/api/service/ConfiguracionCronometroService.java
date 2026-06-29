package com.boxing.api.service;

import com.boxing.api.controller.dto.ConfiguracionCronometroRequestDTO;
import com.boxing.api.controller.dto.ConfiguracionCronometroResponseDTO;

import java.util.List;

public interface ConfiguracionCronometroService {

    List<ConfiguracionCronometroResponseDTO> obtenerPorUsuario(Long usuarioId);

    ConfiguracionCronometroResponseDTO crear(ConfiguracionCronometroRequestDTO dto, Long usuarioId);

    ConfiguracionCronometroResponseDTO actualizar(Long id, ConfiguracionCronometroRequestDTO dto, Long usuarioId);

    void eliminar(Long id, Long usuarioId);
}
