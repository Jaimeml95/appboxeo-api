package com.boxing.api.repository;

import com.boxing.api.model.TimerConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimerConfigurationRepository extends JpaRepository<TimerConfiguration, Long> {

    List<TimerConfiguration> findByUserId(Long userId);

    Optional<TimerConfiguration> findByIdAndUserId(Long id, Long userId);
}
