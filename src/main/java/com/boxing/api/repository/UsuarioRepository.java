package com.boxing.api.repository;

import com.boxing.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Una consulta personalizada muy común e importante para el futuro login/registro.
    // Spring Data JPA la implementa automáticamente leyendo el nombre del método (Query Method).
    Optional<Usuario> findByEmail(String email);

    // Método para comprobar si ya existe un email registrado antes de insertar uno nuevo.
    boolean existsByEmail(String email);
}