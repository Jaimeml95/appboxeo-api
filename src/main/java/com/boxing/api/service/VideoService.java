package com.boxing.api.service;

import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;

import java.util.List;

public interface VideoService {

    List<VideoResponseDTO> obtenerTodos();

    VideoResponseDTO obtenerPorId(Long id);

    VideoResponseDTO crear(VideoRequestDTO dto);

    VideoResponseDTO actualizar(Long id, VideoRequestDTO dto);

    void eliminar(Long id);
}
