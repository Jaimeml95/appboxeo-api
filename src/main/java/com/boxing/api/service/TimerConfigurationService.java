package com.boxing.api.service;

import com.boxing.api.controller.dto.TimerConfigurationRequestDTO;
import com.boxing.api.controller.dto.TimerConfigurationResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TimerConfigurationService {

    List<TimerConfigurationResponseDTO> getByUser(UUID userId);

    TimerConfigurationResponseDTO getById(UUID id, UUID userId);

    TimerConfigurationResponseDTO create(TimerConfigurationRequestDTO dto, UUID userId);

    TimerConfigurationResponseDTO update(UUID id, TimerConfigurationRequestDTO dto, UUID userId);

    void delete(UUID id, UUID userId);
}
