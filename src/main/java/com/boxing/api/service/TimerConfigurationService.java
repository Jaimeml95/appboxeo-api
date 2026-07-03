package com.boxing.api.service;

import com.boxing.api.controller.dto.TimerConfigurationRequestDTO;
import com.boxing.api.controller.dto.TimerConfigurationResponseDTO;

import java.util.List;

public interface TimerConfigurationService {

    List<TimerConfigurationResponseDTO> getByUser(Long userId);

    TimerConfigurationResponseDTO create(TimerConfigurationRequestDTO dto, Long userId);

    TimerConfigurationResponseDTO update(Long id, TimerConfigurationRequestDTO dto, Long userId);

    void delete(Long id, Long userId);
}
