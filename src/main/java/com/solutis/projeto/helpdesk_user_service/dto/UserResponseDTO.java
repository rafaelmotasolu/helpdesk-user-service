package com.solutis.projeto.helpdesk_user_service.dto;

import java.time.LocalDateTime;
import com.solutis.projeto.helpdesk_user_service.entity.User;

public record UserResponseDTO(
    Long id,
    String name,
    String email,
    String role,
    boolean active,
    LocalDateTime createdAt
) {
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole() != null ? user.getRole().getName() : null,
            user.isActive(),
            user.getCreatedAt()
        );
    }
}
