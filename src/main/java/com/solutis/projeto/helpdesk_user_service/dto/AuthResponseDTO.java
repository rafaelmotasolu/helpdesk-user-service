package com.solutis.projeto.helpdesk_user_service.dto;

public record AuthResponseDTO(

    String token,
    String tokenType,
    Long   userId,
    String name,
    String email,
    String role
) {}
