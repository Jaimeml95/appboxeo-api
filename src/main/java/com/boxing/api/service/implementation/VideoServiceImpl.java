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
import java.util.UUID;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoResponseDTO> getAll() {
        return videoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponseDTO getById(UUID id) {
        return toResponse(findVideoById(id));
    }

    @Override
    @Transactional
    public VideoResponseDTO create(VideoRequestDTO dto) {
        Video video = new Video(dto.getTitle(), dto.getDescription(), dto.getType(), dto.getUrl(), dto.getCategory());
        return toResponse(videoRepository.save(video));
    }

    @Override
    @Transactional
    public VideoResponseDTO update(UUID id, VideoRequestDTO dto) {
        Video video = findVideoById(id);
        video.setTitle(dto.getTitle());
        video.setDescription(dto.getDescription());
        video.setType(dto.getType());
        video.setUrl(dto.getUrl());
        video.setCategory(dto.getCategory());
        return toResponse(videoRepository.save(video));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        videoRepository.delete(findVideoById(id));
    }

    private Video findVideoById(UUID id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Video not found"));
    }

    private VideoResponseDTO toResponse(Video v) {
        return new VideoResponseDTO(v.getId(), v.getTitle(), v.getDescription(), v.getType(), v.getUrl(), v.getCategory());
    }
}
