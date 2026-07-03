package com.boxing.api.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequestDTO(
        @NotBlank(message = "ID token must not be blank")
        String idToken
) {}
