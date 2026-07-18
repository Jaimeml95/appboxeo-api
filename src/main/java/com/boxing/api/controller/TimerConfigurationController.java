package com.boxing.api.controller;

import com.boxing.api.controller.dto.TimerConfigurationRequestDTO;
import com.boxing.api.controller.dto.TimerConfigurationResponseDTO;
import com.boxing.api.exception.ErrorResponseDTO;
import com.boxing.api.model.User;
import com.boxing.api.service.TimerConfigurationService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Timer Configurations", description = "Management of per-user timer configurations")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/timer-configurations")
public class TimerConfigurationController {

    private final TimerConfigurationService timerConfigurationService;

    public TimerConfigurationController(TimerConfigurationService timerConfigurationService) {
        this.timerConfigurationService = timerConfigurationService;
    }

    @Operation(summary = "List configurations", description = "Returns all configurations of the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Missing token", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<TimerConfigurationResponseDTO>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(timerConfigurationService.getByUser(user.getId()));
    }

    @Operation(summary = "Get configuration", description = "Returns a single configuration of the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "403", description = "Missing token", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TimerConfigurationResponseDTO> get(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(timerConfigurationService.getById(id, user.getId()));
    }

    @Operation(summary = "Create configuration", description = "Creates a new timer configuration for the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Configuration created"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Missing token", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<TimerConfigurationResponseDTO> create(
            @Valid @RequestBody TimerConfigurationRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(timerConfigurationService.create(dto, user.getId()), HttpStatus.CREATED);
    }

    @Operation(summary = "Update configuration", description = "Updates an existing configuration of the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuration updated"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Missing token", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<TimerConfigurationResponseDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody TimerConfigurationRequestDTO dto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(timerConfigurationService.update(id, dto, user.getId()));
    }

    @Operation(summary = "Delete configuration", description = "Deletes a configuration of the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Configuration deleted"),
            @ApiResponse(responseCode = "403", description = "Missing token", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal User user) {
        timerConfigurationService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
