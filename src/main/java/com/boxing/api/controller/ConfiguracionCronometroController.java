package com.boxing.api.controller;

import com.boxing.api.controller.dto.ConfiguracionCronometroRequestDTO;
import com.boxing.api.controller.dto.ConfiguracionCronometroResponseDTO;
import com.boxing.api.model.Usuario;
import com.boxing.api.service.ConfiguracionCronometroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cronómetro", description = "Gestión de configuraciones de cronómetro por usuario")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/cronometro/configuraciones")
public class ConfiguracionCronometroController {

    private final ConfiguracionCronometroService cronometroService;

    public ConfiguracionCronometroController(ConfiguracionCronometroService cronometroService) {
        this.cronometroService = cronometroService;
    }

    @Operation(summary = "Listar configuraciones", description = "Devuelve todas las configuraciones del usuario autenticado.")
    @GetMapping
    public ResponseEntity<List<ConfiguracionCronometroResponseDTO>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(cronometroService.obtenerPorUsuario(usuario.getId()));
    }

    @Operation(summary = "Crear configuración", description = "Crea una nueva configuración de cronómetro para el usuario autenticado.")
    @PostMapping
    public ResponseEntity<ConfiguracionCronometroResponseDTO> crear(
            @Valid @RequestBody ConfiguracionCronometroRequestDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return new ResponseEntity<>(cronometroService.crear(dto, usuario.getId()), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar configuración", description = "Actualiza una configuración existente del usuario autenticado.")
    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionCronometroResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConfiguracionCronometroRequestDTO dto,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(cronometroService.actualizar(id, dto, usuario.getId()));
    }

    @Operation(summary = "Eliminar configuración", description = "Elimina una configuración del usuario autenticado.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        cronometroService.eliminar(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
