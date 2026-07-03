package com.boxing.api.controller;

import com.boxing.api.controller.dto.GoogleLoginRequestDTO;
import com.boxing.api.controller.dto.LoginRequestDTO;
import com.boxing.api.controller.dto.LoginResponseDTO;
import com.boxing.api.exception.ErrorResponseDTO;
import com.boxing.api.model.User;
import com.boxing.api.service.GoogleTokenVerifier;
import com.boxing.api.service.GoogleUserInfo;
import com.boxing.api.service.JwtService;
import com.boxing.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Google Sign-In for boxers and local login for the admin account")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                           JwtService jwtService, GoogleTokenVerifier googleTokenVerifier) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.googleTokenVerifier = googleTokenVerifier;
    }

    @Operation(summary = "Google Sign-In", description = "Verifies a Google ID token and returns a JWT, creating the account (role BOXER) on first login.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or unverified Google token", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/google")
    public ResponseEntity<LoginResponseDTO> loginWithGoogle(@Valid @RequestBody GoogleLoginRequestDTO dto) {
        GoogleUserInfo googleUser = googleTokenVerifier.verify(dto.idToken());
        User user = userService.findOrCreateByGoogle(googleUser.googleId(), googleUser.email(), googleUser.name());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Admin login", description = "Local email/password login, reserved for the seeded admin account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
        );
        UserDetails userDetails = userService.loadUserByUsername(dto.email());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
