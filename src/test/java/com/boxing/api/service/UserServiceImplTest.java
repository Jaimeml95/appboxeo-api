package com.boxing.api.service;

import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID NON_EXISTING_ID = UUID.randomUUID();

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Test Boxer", "boxer@example.com", "passwordHash", Role.BOXER);
        user.setId(USER_ID);
    }

    @Test
    void findOrCreateByGoogle_newGoogleId_createsUserWithBoxerRole() {
        User created = User.forGoogleSignIn("Test Boxer", "boxer@example.com", "google-sub-123",
                "https://example.com/photo.jpg", Role.BOXER);
        created.setId(USER_ID);
        when(userRepository.findByGoogleId("google-sub-123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(created);

        User result = userService.findOrCreateByGoogle("google-sub-123", "boxer@example.com", "Test Boxer",
                "https://example.com/photo.jpg");

        assertThat(result.getEmail()).isEqualTo("boxer@example.com");
        assertThat(result.getRole()).isEqualTo(Role.BOXER);
        assertThat(result.getGoogleId()).isEqualTo("google-sub-123");
        assertThat(result.getPictureUrl()).isEqualTo("https://example.com/photo.jpg");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findOrCreateByGoogle_existingGoogleId_syncsNameAndPictureFromGoogle() {
        User existing = User.forGoogleSignIn("Old Name", "boxer@example.com", "google-sub-123",
                "https://example.com/old.jpg", Role.BOXER);
        existing.setId(USER_ID);
        when(userRepository.findByGoogleId("google-sub-123")).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.findOrCreateByGoogle("google-sub-123", "boxer@example.com", "New Name",
                "https://example.com/new.jpg");

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getPictureUrl()).isEqualTo("https://example.com/new.jpg");
        verify(userRepository, times(1)).save(existing);
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
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getById(USER_ID);

        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getName()).isEqualTo("Test Boxer");
    }

    @Test
    void getById_nonExisting_throwsException() {
        when(userRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(NON_EXISTING_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void update_existing_updatesNameAndRole() {
        UserUpdateDTO dto = new UserUpdateDTO("Updated Name", Role.ADMIN);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserResponseDTO result = userService.update(USER_ID, dto);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void update_nonExisting_throwsException() {
        UserUpdateDTO dto = new UserUpdateDTO("Updated Name", Role.ADMIN);
        when(userRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(NON_EXISTING_ID, dto))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }

    @Test
    void delete_existing_deletesUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.delete(USER_ID);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(userRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(NON_EXISTING_ID))
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
