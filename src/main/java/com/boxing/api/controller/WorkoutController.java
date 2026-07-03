package com.boxing.api.controller;

import com.boxing.api.controller.dto.ExerciseRequestDTO;
import com.boxing.api.controller.dto.ExerciseResponseDTO;
import com.boxing.api.controller.dto.WorkoutRequestDTO;
import com.boxing.api.controller.dto.WorkoutResponseDTO;
import com.boxing.api.exception.ErrorResponseDTO;
import com.boxing.api.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Workouts", description = "Workout management and their exercises")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    @Operation(summary = "List workouts", description = "Returns all workouts with their exercises.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Missing token or insufficient permissions", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<WorkoutResponseDTO>> list() {
        return ResponseEntity.ok(workoutService.getAll());
    }

    @Operation(summary = "Get workout", description = "Returns the detail of a workout with its exercises.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Missing token or insufficient permissions", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(workoutService.getById(id));
    }

    @Operation(summary = "Create workout", description = "Creates a new workout. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Workout created"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Missing token or role other than ADMIN", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<WorkoutResponseDTO> create(@Valid @RequestBody WorkoutRequestDTO dto) {
        return new ResponseEntity<>(workoutService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update workout", description = "Updates an existing workout. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Missing token or role other than ADMIN", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<WorkoutResponseDTO> update(@PathVariable Long id, @Valid @RequestBody WorkoutRequestDTO dto) {
        return ResponseEntity.ok(workoutService.update(id, dto));
    }

    @Operation(summary = "Delete workout", description = "Deletes a workout and its exercises. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workout deleted"),
            @ApiResponse(responseCode = "403", description = "Missing token or role other than ADMIN", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workoutService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add exercise", description = "Adds an exercise to a workout. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exercise created"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Missing token or role other than ADMIN", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/exercises")
    public ResponseEntity<ExerciseResponseDTO> addExercise(@PathVariable Long id, @Valid @RequestBody ExerciseRequestDTO dto) {
        return new ResponseEntity<>(workoutService.addExercise(id, dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Update exercise", description = "Updates an exercise of a workout. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Missing token or role other than ADMIN", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout or exercise not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/exercises/{exerciseId}")
    public ResponseEntity<ExerciseResponseDTO> updateExercise(@PathVariable Long id, @PathVariable Long exerciseId, @Valid @RequestBody ExerciseRequestDTO dto) {
        return ResponseEntity.ok(workoutService.updateExercise(id, exerciseId, dto));
    }

    @Operation(summary = "Delete exercise", description = "Deletes an exercise from a workout. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exercise deleted"),
            @ApiResponse(responseCode = "403", description = "Missing token or role other than ADMIN", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout or exercise not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/exercises/{exerciseId}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id, @PathVariable Long exerciseId) {
        workoutService.deleteExercise(id, exerciseId);
        return ResponseEntity.noContent().build();
    }
}
