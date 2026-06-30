package com.boxing.api.repository;

import com.boxing.api.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    Optional<Ejercicio> findByIdAndEntrenamientoId(Long id, Long entrenamientoId);
}
