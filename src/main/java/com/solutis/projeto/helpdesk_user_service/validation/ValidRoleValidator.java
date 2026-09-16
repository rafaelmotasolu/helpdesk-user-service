package com.solutis.projeto.helpdesk_user_service.validation;

import com.solutis.projeto.helpdesk_user_service.repository.RoleRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidRoleValidator implements ConstraintValidator<ValidRole, String> {

    @Autowired(required = false)
    private RoleRepository roleRepository;

    @Override
    public boolean isValid(String roleName, ConstraintValidatorContext context) {
        if (roleName == null || roleName.isBlank()) {
            return true; // Deixa a validação de nulo/vazio para @NotBlank
        }

        if (roleRepository == null) {
            return true; // Fallback para de teste sem persistência carregada
        }

        return roleRepository.existsByNameIgnoreCase(roleName.trim());
    }
}

