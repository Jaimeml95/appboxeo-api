package com.boxing.api.controller.dto;

import com.boxing.api.model.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioActualizarDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @NotNull(message = "El rol no puede estar vacío")
        Rol rol
) {}
