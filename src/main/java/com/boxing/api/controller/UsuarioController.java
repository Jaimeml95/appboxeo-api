package com.boxing.api.controller;

import com.boxing.api.controller.dto.UsuarioAdminCrearDTO;
import com.boxing.api.model.Usuario;
import com.boxing.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios — solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Crear usuario", description = "Crea un usuario con el rol especificado. Solo ADMIN.")
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody UsuarioAdminCrearDTO dto) {
        return new ResponseEntity<>(usuarioService.crearUsuarioPorAdmin(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios registrados. Solo ADMIN.")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }
}
