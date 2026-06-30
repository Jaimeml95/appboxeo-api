package com.boxing.api.service;

import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;
import com.boxing.api.model.CategoriaVideo;
import com.boxing.api.model.TipoVideo;
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

    private Video video;
    private VideoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        video = new Video("Jab cruzado", "Técnica básica de jab", TipoVideo.YOUTUBE, "https://youtube.com/watch?v=abc", CategoriaVideo.TECNICA);
        video.setId(1L);

        requestDTO = new VideoRequestDTO();
        requestDTO.setTitulo("Jab cruzado");
        requestDTO.setDescripcion("Técnica básica de jab");
        requestDTO.setTipo(TipoVideo.YOUTUBE);
        requestDTO.setUrl("https://youtube.com/watch?v=abc");
        requestDTO.setCategoria(CategoriaVideo.TECNICA);
    }

    @Test
    void obtenerTodos_devuelveListaDeVideos() {
        when(videoRepository.findAll()).thenReturn(List.of(video));

        List<VideoResponseDTO> resultado = videoService.obtenerTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTitulo()).isEqualTo("Jab cruzado");
        assertThat(resultado.get(0).getCategoria()).isEqualTo(CategoriaVideo.TECNICA);
    }

    @Test
    void obtenerTodos_listaVacia_devuelveListaVacia() {
        when(videoRepository.findAll()).thenReturn(List.of());

        List<VideoResponseDTO> resultado = videoService.obtenerTodos();

        assertThat(resultado).isEmpty();
    }

    @Test
    void obtenerPorId_existente_devuelveVideo() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        VideoResponseDTO resultado = videoService.obtenerPorId(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Jab cruzado");
        assertThat(resultado.getTipo()).isEqualTo(TipoVideo.YOUTUBE);
    }

    @Test
    void obtenerPorId_noExistente_lanzaExcepcion() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.obtenerPorId(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Video no encontrado");
    }

    @Test
    void crear_guardaYDevuelveVideo() {
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        VideoResponseDTO resultado = videoService.crear(requestDTO);

        assertThat(resultado.getTitulo()).isEqualTo("Jab cruzado");
        assertThat(resultado.getUrl()).isEqualTo("https://youtube.com/watch?v=abc");
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        VideoRequestDTO dtoActualizado = new VideoRequestDTO();
        dtoActualizado.setTitulo("Uppercut");
        dtoActualizado.setDescripcion("Golpe ascendente");
        dtoActualizado.setTipo(TipoVideo.PROPIO);
        dtoActualizado.setUrl("https://miservidor.com/uppercut.mp4");
        dtoActualizado.setCategoria(CategoriaVideo.FUERZA);

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenAnswer(inv -> inv.getArgument(0));

        VideoResponseDTO resultado = videoService.actualizar(1L, dtoActualizado);

        assertThat(resultado.getTitulo()).isEqualTo("Uppercut");
        assertThat(resultado.getTipo()).isEqualTo(TipoVideo.PROPIO);
        assertThat(resultado.getCategoria()).isEqualTo(CategoriaVideo.FUERZA);
    }

    @Test
    void actualizar_noExistente_lanzaExcepcion() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.actualizar(99L, requestDTO))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Video no encontrado");
    }

    @Test
    void eliminar_existente_eliminaVideo() {
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        videoService.eliminar(1L);

        verify(videoRepository, times(1)).delete(video);
    }

    @Test
    void eliminar_noExistente_lanzaExcepcion() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> videoService.eliminar(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Video no encontrado");
    }
}
