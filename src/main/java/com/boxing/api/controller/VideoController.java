package com.boxing.api.controller;

import com.boxing.api.controller.dto.VideoRequestDTO;
import com.boxing.api.controller.dto.VideoResponseDTO;
import com.boxing.api.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Videos", description = "Gestión de videos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @Operation(summary = "Listar videos", description = "Devuelve todos los videos.")
    @GetMapping
    public ResponseEntity<List<VideoResponseDTO>> listar() {
        return ResponseEntity.ok(videoService.obtenerTodos());
    }

    @Operation(summary = "Obtener video", description = "Devuelve el detalle de un video.")
    @GetMapping("/{id}")
    public ResponseEntity<VideoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear video", description = "Crea un nuevo video. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<VideoResponseDTO> crear(@Valid @RequestBody VideoRequestDTO dto) {
        return new ResponseEntity<>(videoService.crear(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar video", description = "Actualiza un video existente. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<VideoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody VideoRequestDTO dto) {
        return ResponseEntity.ok(videoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar video", description = "Elimina un video. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        videoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
