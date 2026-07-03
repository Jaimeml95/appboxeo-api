package com.boxing.api.service;

import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserAdminCreateDTO;
import com.boxing.api.controller.dto.UserRegistrationDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
import com.boxing.api.exception.ResourceAlreadyExistsException;
import com.boxing.api.model.Role;
import com.boxing.api.model.User;
import com.boxing.api.repository.UserRepository;
import com.boxing.api.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test Boxer", "boxer@example.com", "passwordHash", Role.BOXER);
        user.setId(1L);
    }

    @Test
    void register_newEmail_createsUserWithBoxerRole() {
        UserRegistrationDTO dto = new UserRegistrationDTO("Test Boxer", "boxer@example.com", "password123");
        when(userRepository.existsByEmail("boxer@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("passwordHash");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO result = userService.register(dto);

        assertThat(result.getEmail()).isEqualTo("boxer@example.com");
        assertThat(result.getRole()).isEqualTo(Role.BOXER);
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        UserRegistrationDTO dto = new UserRegistrationDTO("Test Boxer", "boxer@example.com", "password123");
        when(userRepository.existsByEmail("boxer@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_newEmail_createsUserWithGivenRole() {
        UserAdminCreateDTO dto = new UserAdminCreateDTO("New Admin", "admin2@example.com", "password123", Role.ADMIN);
        User admin = new User("New Admin", "admin2@example.com", "hash", Role.ADMIN);
        admin.setId(2L);

        when(userRepository.existsByEmail("admin2@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(admin);

        UserResponseDTO result = userService.createUser(dto);

        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.getEmail()).isEqualTo("admin2@example.com");
    }

    @Test
    void createUser_emailAlreadyExists_throwsException() {
        UserAdminCreateDTO dto = new UserAdminCreateDTO("New Admin", "boxer@example.com", "password123", Role.ADMIN);
        when(userRepository.existsByEmail("boxer@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void getAll_returnsListOfUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("boxer@example.com");
    }

    @Test
    void getById_existing_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Boxer");
    }

    @Test
    void getById_nonExisting_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void update_existing_updatesNameAndRole() {
        UserUpdateDTO dto = new UserUpdateDTO("Updated Name", Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = userService.update(1L, dto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void update_nonExisting_throwsException() {
        UserUpdateDTO dto = new UserUpdateDTO("Updated Name", Role.ADMIN);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void delete_existing_deletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void loadUserByUsername_existing_returnsUserDetails() {
        when(userRepository.findByEmail("boxer@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("boxer@example.com");

        assertThat(result.getUsername()).isEqualTo("boxer@example.com");
    }

    @Test
    void loadUserByUsername_nonExisting_throwsException() {
        when(userRepository.findByEmail("doesnotexist@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("doesnotexist@example.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
