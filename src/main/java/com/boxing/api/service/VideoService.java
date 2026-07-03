package com.boxing.api.service;

import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;

import java.util.List;

public interface VideoService {

    List<VideoResponseDTO> getAll();

    VideoResponseDTO getById(Long id);

    VideoResponseDTO create(VideoRequestDTO dto);

    VideoResponseDTO update(Long id, VideoRequestDTO dto);

    void delete(Long id);
}
