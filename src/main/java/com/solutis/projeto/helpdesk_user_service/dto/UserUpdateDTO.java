package com.solutis.projeto.helpdesk_user_service.dto;

import com.solutis.projeto.helpdesk_user_service.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
    
    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    String name,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,

    @NotNull(message = "O perfil (role) é obrigatório")
    Role role
) {}
