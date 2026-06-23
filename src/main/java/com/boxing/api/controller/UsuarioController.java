package com.boxing.api.controller;

import com.boxing.api.controller.dto.UsuarioRegistroDTO;
import com.boxing.api.model.Usuario;
import com.boxing.api.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestión de usuarios de la aplicación de boxeo")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Inyección por constructor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario. Devuelve 409 si el email ya existe.")
    @PostMapping
    public ResponseEntity<Usuario> registrarUsuario(@Valid @RequestBody UsuarioRegistroDTO dto) {
        // Mapeo manual del DTO a la Entidad (Mantiene el control sin librerías externas por ahora)
        Usuario usuarioAInsertar = new Usuario(dto.getNombre(), dto.getEmail());

        Usuario usuarioRegistrado = usuarioService.registrarUsuario(usuarioAInsertar);

        // Devolvemos un HTTP 201 Created junto al objeto guardado
        return new ResponseEntity<>(usuarioRegistrado, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios registrados.")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }
}