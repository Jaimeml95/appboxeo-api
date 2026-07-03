package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.TimerConfigurationRequestDTO;
import com.boxing.api.controller.dto.TimerConfigurationResponseDTO;
import com.boxing.api.model.TimerConfiguration;
import com.boxing.api.model.User;
import com.boxing.api.repository.TimerConfigurationRepository;
import com.boxing.api.repository.UserRepository;
import com.boxing.api.service.TimerConfigurationService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TimerConfigurationServiceImpl implements TimerConfigurationService {

    private final TimerConfigurationRepository timerConfigurationRepository;
    private final UserRepository userRepository;

    public TimerConfigurationServiceImpl(TimerConfigurationRepository timerConfigurationRepository,
                                          UserRepository userRepository) {
        this.timerConfigurationRepository = timerConfigurationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimerConfigurationResponseDTO> getByUser(Long userId) {
        return timerConfigurationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TimerConfigurationResponseDTO create(TimerConfigurationRequestDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        TimerConfiguration config = new TimerConfiguration(
                dto.getName(), dto.getRounds(), dto.getRoundDuration(), dto.getRest(), user
        );

        return toResponse(timerConfigurationRepository.save(config));
    }

    @Override
    @Transactional
    public TimerConfigurationResponseDTO update(Long id, TimerConfigurationRequestDTO dto, Long userId) {
        TimerConfiguration config = timerConfigurationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Configuration not found"));

        config.setName(dto.getName());
        config.setRounds(dto.getRounds());
        config.setRoundDuration(dto.getRoundDuration());
        config.setRest(dto.getRest());

        return toResponse(timerConfigurationRepository.save(config));
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        TimerConfiguration config = timerConfigurationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Configuration not found"));

        timerConfigurationRepository.delete(config);
    }

    private TimerConfigurationResponseDTO toResponse(TimerConfiguration config) {
        return new TimerConfigurationResponseDTO(
                config.getId(),
                config.getName(),
                config.getRounds(),
                config.getRoundDuration(),
                config.getRest()
        );
    }
}
