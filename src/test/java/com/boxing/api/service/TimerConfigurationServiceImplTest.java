package com.boxing.api.service;

import com.boxing.api.controller.dto.TimerConfigurationRequestDTO;
import com.boxing.api.controller.dto.TimerConfigurationResponseDTO;
import com.boxing.api.model.TimerConfiguration;
import com.boxing.api.model.Role;
import com.boxing.api.model.User;
import com.boxing.api.repository.TimerConfigurationRepository;
import com.boxing.api.repository.UserRepository;
import com.boxing.api.service.implementation.TimerConfigurationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimerConfigurationServiceImplTest {

    @Mock
    private TimerConfigurationRepository timerConfigurationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TimerConfigurationServiceImpl timerConfigurationService;

    private User user;
    private TimerConfiguration config;
    private TimerConfigurationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = new User("Carlos", "carlos@boxing.com", "password123", Role.BOXER);
        user.setId(1L);

        config = new TimerConfiguration("Classic Rounds", 3, 180, 60, user);
        config.setId(1L);

        requestDTO = new TimerConfigurationRequestDTO();
        requestDTO.setName("Classic Rounds");
        requestDTO.setRounds(3);
        requestDTO.setRoundDuration(180);
        requestDTO.setRest(60);
    }

    @Test
    void getByUser_returnsUserConfigurations() {
        when(timerConfigurationRepository.findByUserId(1L)).thenReturn(List.of(config));

        List<TimerConfigurationResponseDTO> result = timerConfigurationService.getByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Classic Rounds");
        assertThat(result.get(0).getRounds()).isEqualTo(3);
    }

    @Test
    void getByUser_noConfigurations_returnsEmptyList() {
        when(timerConfigurationRepository.findByUserId(1L)).thenReturn(List.of());

        List<TimerConfigurationResponseDTO> result = timerConfigurationService.getByUser(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_existingUser_savesAndReturnsConfiguration() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(timerConfigurationRepository.save(any(TimerConfiguration.class))).thenReturn(config);

        TimerConfigurationResponseDTO result = timerConfigurationService.create(requestDTO, 1L);

        assertThat(result.getName()).isEqualTo("Classic Rounds");
        assertThat(result.getRoundDuration()).isEqualTo(180);
        assertThat(result.getRest()).isEqualTo(60);
        verify(timerConfigurationRepository, times(1)).save(any(TimerConfiguration.class));
    }

    @Test
    void create_nonExistingUser_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timerConfigurationService.create(requestDTO, 99L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void update_existing_updatesFields() {
        TimerConfigurationRequestDTO updatedDTO = new TimerConfigurationRequestDTO();
        updatedDTO.setName("Intense Rounds");
        updatedDTO.setRounds(6);
        updatedDTO.setRoundDuration(120);
        updatedDTO.setRest(30);

        when(timerConfigurationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(config));
        when(timerConfigurationRepository.save(any(TimerConfiguration.class))).thenAnswer(inv -> inv.getArgument(0));

        TimerConfigurationResponseDTO result = timerConfigurationService.update(1L, updatedDTO, 1L);

        assertThat(result.getName()).isEqualTo("Intense Rounds");
        assertThat(result.getRounds()).isEqualTo(6);
        assertThat(result.getRoundDuration()).isEqualTo(120);
    }

    @Test
    void update_nonExisting_throwsException() {
        when(timerConfigurationRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timerConfigurationService.update(99L, requestDTO, 1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Configuration not found");
    }

    @Test
    void delete_existing_deletesConfiguration() {
        when(timerConfigurationRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(config));

        timerConfigurationService.delete(1L, 1L);

        verify(timerConfigurationRepository, times(1)).delete(config);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(timerConfigurationRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timerConfigurationService.delete(99L, 1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Configuration not found");
    }
}
