package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;
import com.boxing.api.model.Video;
import com.boxing.api.repository.VideoRepository;
import com.boxing.api.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoResponseDTO> obtenerTodos() {
        return videoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponseDTO obtenerPorId(Long id) {
        return toResponse(findVideoById(id));
    }

    @Override
    @Transactional
    public VideoResponseDTO crear(VideoRequestDTO dto) {
        Video video = new Video(dto.getTitulo(), dto.getDescripcion(), dto.getTipo(), dto.getUrl(), dto.getCategoria());
        return toResponse(videoRepository.save(video));
    }

    @Override
    @Transactional
    public VideoResponseDTO actualizar(Long id, VideoRequestDTO dto) {
        Video video = findVideoById(id);
        video.setTitulo(dto.getTitulo());
        video.setDescripcion(dto.getDescripcion());
        video.setTipo(dto.getTipo());
        video.setUrl(dto.getUrl());
        video.setCategoria(dto.getCategoria());
        return toResponse(videoRepository.save(video));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        videoRepository.delete(findVideoById(id));
    }

    private Video findVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Video no encontrado"));
    }

    private VideoResponseDTO toResponse(Video v) {
        return new VideoResponseDTO(v.getId(), v.getTitulo(), v.getDescripcion(), v.getTipo(), v.getUrl(), v.getCategoria());
    }
}
