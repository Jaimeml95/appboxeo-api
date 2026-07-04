package com.boxing.api.service;

import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
import com.boxing.api.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    // Returns the entity (not a response DTO) because the caller needs it as
    // UserDetails to issue a JWT right away. Syncs name/pictureUrl from
    // Google on every login, not just on first creation.
    User findOrCreateByGoogle(String googleId, String email, String name, String pictureUrl);

    List<UserResponseDTO> getAll();

    UserResponseDTO getById(UUID id);

    UserResponseDTO update(UUID id, UserUpdateDTO dto);

    void delete(UUID id);
}
