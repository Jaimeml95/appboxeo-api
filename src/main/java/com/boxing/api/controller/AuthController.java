package com.boxing.api.controller;

import com.boxing.api.controller.dto.LoginRequestDTO;
import com.boxing.api.controller.dto.LoginResponseDTO;
import com.boxing.api.controller.dto.UsuarioRegistroDTO;
import com.boxing.api.exception.ErrorResponseDTO;
import com.boxing.api.model.Usuario;
import com.boxing.api.service.JwtService;
import com.boxing.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticación", description = "Registro público de boxeadores y login")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Registro de boxeador", description = "Crea una cuenta pública con rol BOXEADOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registro(@Valid @RequestBody UsuarioRegistroDTO dto) {
        return new ResponseEntity<>(usuarioService.registrarBoxeador(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Login", description = "Devuelve un token JWT si las credenciales son correctas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        UserDetails userDetails = usuarioService.loadUserByUsername(dto.email());
        String token = jwtService.generarToken(userDetails);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
