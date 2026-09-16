package com.solutis.projeto.helpdesk_user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.solutis.projeto.helpdesk_user_service.dto.UserCreateDTO;
import com.solutis.projeto.helpdesk_user_service.dto.UserResponseDTO;
import com.solutis.projeto.helpdesk_user_service.dto.UserUpdateDTO;
import com.solutis.projeto.helpdesk_user_service.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuários", description = "Endpoints de gerenciamento de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Criar um novo usuário")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO dto) {
        UserResponseDTO created = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados de um usuário")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar um usuário")
    public ResponseEntity<Void> inactivate(@PathVariable Long id) {
        userService.inactivate(id);
        return ResponseEntity.noContent().build();
    }
}
