package com.solutis.projeto.helpdesk_user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.solutis.projeto.helpdesk_user_service.dto.AuthRequestDTO;
import com.solutis.projeto.helpdesk_user_service.dto.AuthResponseDTO;
import com.solutis.projeto.helpdesk_user_service.service.AuthService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints de login e obtenção de token JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login e obter token JWT")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
