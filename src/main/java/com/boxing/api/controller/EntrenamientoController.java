package com.boxing.api.controller;

import com.boxing.api.controller.dto.EjercicioRequestDTO;
import com.boxing.api.controller.dto.EjercicioResponseDTO;
import com.boxing.api.controller.dto.EntrenamientoRequestDTO;
import com.boxing.api.controller.dto.EntrenamientoResponseDTO;
import com.boxing.api.service.EntrenamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Entrenamientos", description = "Gestión de entrenamientos y sus ejercicios")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/entrenamientos")
public class EntrenamientoController {

    private final EntrenamientoService entrenamientoService;

    public EntrenamientoController(EntrenamientoService entrenamientoService) {
        this.entrenamientoService = entrenamientoService;
    }

    @Operation(summary = "Listar entrenamientos", description = "Devuelve todos los entrenamientos con sus ejercicios.")
    @GetMapping
    public ResponseEntity<List<EntrenamientoResponseDTO>> listar() {
        return ResponseEntity.ok(entrenamientoService.obtenerTodos());
    }

    @Operation(summary = "Obtener entrenamiento", description = "Devuelve el detalle de un entrenamiento con sus ejercicios.")
    @GetMapping("/{id}")
    public ResponseEntity<EntrenamientoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear entrenamiento", description = "Crea un nuevo entrenamiento. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EntrenamientoResponseDTO> crear(@Valid @RequestBody EntrenamientoRequestDTO dto) {
        return new ResponseEntity<>(entrenamientoService.crear(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar entrenamiento", description = "Actualiza un entrenamiento existente. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EntrenamientoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EntrenamientoRequestDTO dto) {
        return ResponseEntity.ok(entrenamientoService.actualizar(id, dto));
    }

    @Operation(summary = "Eliminar entrenamiento", description = "Elimina un entrenamiento y sus ejercicios. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entrenamientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Añadir ejercicio", description = "Añade un ejercicio a un entrenamiento. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/ejercicios")
    public ResponseEntity<EjercicioResponseDTO> agregarEjercicio(@PathVariable Long id, @Valid @RequestBody EjercicioRequestDTO dto) {
        return new ResponseEntity<>(entrenamientoService.agregarEjercicio(id, dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar ejercicio", description = "Actualiza un ejercicio de un entrenamiento. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/ejercicios/{ejercicioId}")
    public ResponseEntity<EjercicioResponseDTO> actualizarEjercicio(@PathVariable Long id, @PathVariable Long ejercicioId, @Valid @RequestBody EjercicioRequestDTO dto) {
        return ResponseEntity.ok(entrenamientoService.actualizarEjercicio(id, ejercicioId, dto));
    }

    @Operation(summary = "Eliminar ejercicio", description = "Elimina un ejercicio de un entrenamiento. Solo ADMIN.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/ejercicios/{ejercicioId}")
    public ResponseEntity<Void> eliminarEjercicio(@PathVariable Long id, @PathVariable Long ejercicioId) {
        entrenamientoService.eliminarEjercicio(id, ejercicioId);
        return ResponseEntity.noContent().build();
    }
}
