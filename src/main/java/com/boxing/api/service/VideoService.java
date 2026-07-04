package com.boxing.api.service;

import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface VideoService {

    List<VideoResponseDTO> getAll();

    VideoResponseDTO getById(UUID id);

    VideoResponseDTO create(VideoRequestDTO dto);

    VideoResponseDTO update(UUID id, VideoRequestDTO dto);

    void delete(UUID id);
}
