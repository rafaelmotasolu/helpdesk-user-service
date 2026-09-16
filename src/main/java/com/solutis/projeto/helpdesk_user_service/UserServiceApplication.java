package com.solutis.projeto.helpdesk_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.solutis.projeto.helpdesk_user_service.entity.Role;
import com.solutis.projeto.helpdesk_user_service.entity.User;
import com.solutis.projeto.helpdesk_user_service.repository.UserRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@SpringBootApplication
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


    /**
     * Seed para criar um ADMIN inicial caso a base esteja vazia.
     * Permite logar no sistema logo na primeira inicialização.
     */

    
    @Bean
    @ConditionalOnBean(UserRepository.class)
    public CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@helpdesk.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User(
                    "Administrador do Sistema",
                    adminEmail,
                    passwordEncoder.encode("admin123"),
                    Role.ADMIN
                );
                userRepository.save(admin);
                System.out.println(">>> [SEED] Usuário ADMIN criado: admin@helpdesk.com / admin123");
            }
        };
    }
}
