package com.boxing.api.repository;

import com.boxing.api.model.ConfiguracionCronometro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConfiguracionCronometroRepository extends JpaRepository<ConfiguracionCronometro, Long> {

    List<ConfiguracionCronometro> findByUsuarioId(Long usuarioId);

    Optional<ConfiguracionCronometro> findByIdAndUsuarioId(Long id, Long usuarioId);
}
