package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserAdminCreateDTO;
import com.boxing.api.controller.dto.UserRegistrationDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
import com.boxing.api.exception.ResourceAlreadyExistsException;
import com.boxing.api.model.Role;
import com.boxing.api.model.User;
import com.boxing.api.repository.UserRepository;
import com.boxing.api.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponseDTO register(UserRegistrationDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email '" + dto.getEmail() + "' is already registered");
        }
        User user = new User(dto.getName(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()), Role.BOXER);
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserAdminCreateDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new ResourceAlreadyExistsException("Email '" + dto.email() + "' is already registered");
        }
        User user = new User(dto.name(), dto.email(), passwordEncoder.encode(dto.password()), dto.role());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        return toResponse(findUserById(id));
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        User user = findUserById(id);
        user.setName(dto.name());
        user.setRole(dto.role());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.delete(findUserById(id));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private UserResponseDTO toResponse(User u) {
        return new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getCreatedAt());
    }
}
