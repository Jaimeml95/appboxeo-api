package com.boxing.api.service;

import com.boxing.api.controller.dto.UserUpdateDTO;
import com.boxing.api.controller.dto.UserAdminCreateDTO;
import com.boxing.api.controller.dto.UserRegistrationDTO;
import com.boxing.api.controller.dto.UserResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserResponseDTO register(UserRegistrationDTO dto);

    UserResponseDTO createUser(UserAdminCreateDTO dto);

    List<UserResponseDTO> getAll();

    UserResponseDTO getById(Long id);

    UserResponseDTO update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}
