package com.boxing.api.service.implementation;

import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
import com.boxing.api.exception.ProtectedAdminOperationException;
import com.boxing.api.model.Role;
import com.boxing.api.model.User;
import com.boxing.api.repository.UserRepository;
import com.boxing.api.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${admin.email}")
    private String seededAdminEmail;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User findOrCreateByGoogle(String googleId, String email, String name, String pictureUrl) {
        return userRepository.findByGoogleId(googleId)
                .map(user -> {
                    user.setName(name);
                    user.setPictureUrl(pictureUrl);
                    return userRepository.save(user);
                })
                .orElseGet(() -> userRepository.save(User.forGoogleSignIn(name, email, googleId, pictureUrl, Role.BOXER)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getById(UUID id) {
        return toResponse(findUserById(id));
    }

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDTO update(UUID id, UserUpdateDTO dto) {
        User user = findUserById(id);
        if (dto.role() != Role.ADMIN && isSeededAdmin(user)) {
            throw new ProtectedAdminOperationException("The seeded admin account cannot be demoted");
        }
        user.setName(dto.name());
        user.setRole(dto.role());
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User user = findUserById(id);
        if (isSeededAdmin(user)) {
            throw new ProtectedAdminOperationException("The seeded admin account cannot be deleted");
        }
        userRepository.delete(user);
    }

    private boolean isSeededAdmin(User user) {
        return seededAdminEmail.equalsIgnoreCase(user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private UserResponseDTO toResponse(User u) {
        return new UserResponseDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getPictureUrl(), u.getCreatedAt());
    }
}
