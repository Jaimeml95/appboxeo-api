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
import java.util.UUID;

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

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NON_EXISTING_USER_ID = UUID.randomUUID();
    private static final UUID CONFIG_ID = UUID.randomUUID();
    private static final UUID NON_EXISTING_CONFIG_ID = UUID.randomUUID();

    private User user;
    private TimerConfiguration config;
    private TimerConfigurationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        user = new User("Carlos", "carlos@boxing.com", "password123", Role.BOXER);
        user.setId(USER_ID);

        config = new TimerConfiguration("Classic Rounds", 3, 180, 60, true, true, user);
        config.setId(CONFIG_ID);

        requestDTO = new TimerConfigurationRequestDTO();
        requestDTO.setName("Classic Rounds");
        requestDTO.setRounds(3);
        requestDTO.setRoundDuration(180);
        requestDTO.setRest(60);
        requestDTO.setWarnBeforeEnd(true);
        requestDTO.setBellSound(true);
    }

    @Test
    void getByUser_returnsUserConfigurations() {
        when(timerConfigurationRepository.findByUserId(USER_ID)).thenReturn(List.of(config));

        List<TimerConfigurationResponseDTO> result = timerConfigurationService.getByUser(USER_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Classic Rounds");
        assertThat(result.get(0).getRounds()).isEqualTo(3);
    }

    @Test
    void getByUser_noConfigurations_returnsEmptyList() {
        when(timerConfigurationRepository.findByUserId(USER_ID)).thenReturn(List.of());

        List<TimerConfigurationResponseDTO> result = timerConfigurationService.getByUser(USER_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void getById_existing_returnsConfiguration() {
        when(timerConfigurationRepository.findByIdAndUserId(CONFIG_ID, USER_ID)).thenReturn(Optional.of(config));

        TimerConfigurationResponseDTO result = timerConfigurationService.getById(CONFIG_ID, USER_ID);

        assertThat(result.getName()).isEqualTo("Classic Rounds");
    }

    @Test
    void getById_nonExisting_throwsException() {
        when(timerConfigurationRepository.findByIdAndUserId(NON_EXISTING_CONFIG_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timerConfigurationService.getById(NON_EXISTING_CONFIG_ID, USER_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Configuration not found");
    }

    @Test
    void create_existingUser_savesAndReturnsConfiguration() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(timerConfigurationRepository.save(any(TimerConfiguration.class))).thenReturn(config);

        TimerConfigurationResponseDTO result = timerConfigurationService.create(requestDTO, USER_ID);

        assertThat(result.getName()).isEqualTo("Classic Rounds");
        assertThat(result.getRoundDuration()).isEqualTo(180);
        assertThat(result.getRest()).isEqualTo(60);
        verify(timerConfigurationRepository, times(1)).save(any(TimerConfiguration.class));
    }

    @Test
    void create_nonExistingUser_throwsException() {
        when(userRepository.findById(NON_EXISTING_USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timerConfigurationService.create(requestDTO, NON_EXISTING_USER_ID))
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
        updatedDTO.setWarnBeforeEnd(false);
        updatedDTO.setBellSound(false);

        when(timerConfigurationRepository.findByIdAndUserId(CONFIG_ID, USER_ID)).thenReturn(Optional.of(config));
        when(timerConfigurationRepository.save(any(TimerConfiguration.class))).thenAnswer(inv -> inv.getArgument(0));

        TimerConfigurationResponseDTO result = timerConfigurationService.update(CONFIG_ID, updatedDTO, USER_ID);

        assertThat(result.getName()).isEqualTo("Intense Rounds");
        assertThat(result.getRounds()).isEqualTo(6);
        assertThat(result.getRoundDuration()).isEqualTo(120);
    }

    @Test
    void update_nonExisting_throwsException() {
        when(timerConfigurationRepository.findByIdAndUserId(NON_EXISTING_CONFIG_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timerConfigurationService.update(NON_EXISTING_CONFIG_ID, requestDTO, USER_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Configuration not found");
    }

    @Test
    void delete_existing_deletesConfiguration() {
        when(timerConfigurationRepository.findByIdAndUserId(CONFIG_ID, USER_ID)).thenReturn(Optional.of(config));

        timerConfigurationService.delete(CONFIG_ID, USER_ID);

        verify(timerConfigurationRepository, times(1)).delete(config);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(timerConfigurationRepository.findByIdAndUserId(NON_EXISTING_CONFIG_ID, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timerConfigurationService.delete(NON_EXISTING_CONFIG_ID, USER_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Configuration not found");
    }
}
