package com.boxing.api.repository;

import com.boxing.api.model.TimerConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimerConfigurationRepository extends JpaRepository<TimerConfiguration, UUID> {

    List<TimerConfiguration> findByUserId(UUID userId);

    Optional<TimerConfiguration> findByIdAndUserId(UUID id, UUID userId);
}
