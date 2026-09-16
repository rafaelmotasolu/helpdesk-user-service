package com.solutis.projeto.helpdesk_user_service.dto;

import com.solutis.projeto.helpdesk_user_service.entity.Role;

public record AuthResponseDTO(

    String token,
    String tokenType,
    Long   userId,
    String name,
    String email,
    Role   role
) {}
