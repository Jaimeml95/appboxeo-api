package com.boxing.api.service;

import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
import com.boxing.api.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    // Returns the entity (not a response DTO) because the caller needs it as
    // UserDetails to issue a JWT right away. Syncs name/pictureUrl from
    // Google on every login, not just on first creation.
    User findOrCreateByGoogle(String googleId, String email, String name, String pictureUrl);

    List<UserResponseDTO> getAll();

    UserResponseDTO getById(Long id);

    UserResponseDTO update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}
