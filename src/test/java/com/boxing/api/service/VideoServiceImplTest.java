package com.boxing.api.service;

import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;
import com.boxing.api.model.VideoCategory;
import com.boxing.api.model.VideoType;
import com.boxing.api.model.Video;
import com.boxing.api.repository.VideoRepository;
import com.boxing.api.service.implementation.VideoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoServiceImpl videoService;

    private static final UUID VIDEO_ID = UUID.randomUUID();
    private static final UUID NON_EXISTING_ID = UUID.randomUUID();

    private Video video;
    private VideoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        video = new Video("Jab Cross", "Basic jab technique", VideoType.YOUTUBE, "https://youtube.com/watch?v=abc", VideoCategory.TECHNIQUE);
        video.setId(VIDEO_ID);

        requestDTO = new VideoRequestDTO();
        requestDTO.setTitle("Jab Cross");
        requestDTO.setDescription("Basic jab technique");
        requestDTO.setType(VideoType.YOUTUBE);
        requestDTO.setUrl("https://youtube.com/watch?v=abc");
        requestDTO.setCategory(VideoCategory.TECHNIQUE);
    }

    @Test
    void getAll_returnsListOfVideos() {
        when(videoRepository.findAll()).thenReturn(List.of(video));

        List<VideoResponseDTO> result = videoService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Jab Cross");
        assertThat(result.get(0).getCategory()).isEqualTo(VideoCategory.TECHNIQUE);
    }

    @Test
    void getAll_emptyList_returnsEmptyList() {
        when(videoRepository.findAll()).thenReturn(List.of());

        List<VideoResponseDTO> result = videoService.getAll();

        assertThat(result).isEmpty();
    }

    @Test
    void getById_existing_returnsVideo() {
        when(videoRepository.findById(VIDEO_ID)).thenReturn(Optional.of(video));

        VideoResponseDTO result = videoService.getById(VIDEO_ID);

        assertThat(result.getId()).isEqualTo(VIDEO_ID);
        assertThat(result.getTitle()).isEqualTo("Jab Cross");
        assertThat(result.getType()).isEqualTo(VideoType.YOUTUBE);
    }

    @Test
    void getById_nonExisting_throwsException() {
        when(videoRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.getById(NON_EXISTING_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Video not found");
    }

    @Test
    void create_savesAndReturnsVideo() {
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        VideoResponseDTO result = videoService.create(requestDTO);

        assertThat(result.getTitle()).isEqualTo("Jab Cross");
        assertThat(result.getUrl()).isEqualTo("https://youtube.com/watch?v=abc");
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    void update_existing_updatesFields() {
        VideoRequestDTO updatedDTO = new VideoRequestDTO();
        updatedDTO.setTitle("Uppercut");
        updatedDTO.setDescription("Upward strike");
        updatedDTO.setType(VideoType.OWN);
        updatedDTO.setUrl("https://myserver.com/uppercut.mp4");
        updatedDTO.setCategory(VideoCategory.STRENGTH);

        when(videoRepository.findById(VIDEO_ID)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenAnswer(inv -> inv.getArgument(0));

        VideoResponseDTO result = videoService.update(VIDEO_ID, updatedDTO);

        assertThat(result.getTitle()).isEqualTo("Uppercut");
        assertThat(result.getType()).isEqualTo(VideoType.OWN);
        assertThat(result.getCategory()).isEqualTo(VideoCategory.STRENGTH);
    }

    @Test
    void update_nonExisting_throwsException() {
        when(videoRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.update(NON_EXISTING_ID, requestDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Video not found");
    }

    @Test
    void delete_existing_deletesVideo() {
        when(videoRepository.findById(VIDEO_ID)).thenReturn(Optional.of(video));

        videoService.delete(VIDEO_ID);

        verify(videoRepository, times(1)).delete(video);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(videoRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.delete(NON_EXISTING_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Video not found");
    }
}
