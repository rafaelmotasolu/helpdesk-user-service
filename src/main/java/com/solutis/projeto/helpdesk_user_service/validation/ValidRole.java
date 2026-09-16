package com.solutis.projeto.helpdesk_user_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidRoleValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRole {

    String message() default "O perfil (role) informado não existe na base de dados";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

