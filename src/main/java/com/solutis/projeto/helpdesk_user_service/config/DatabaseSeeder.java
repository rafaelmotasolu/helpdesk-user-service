package com.solutis.projeto.helpdesk_user_service.config;

import com.solutis.projeto.helpdesk_user_service.entity.Role;
import com.solutis.projeto.helpdesk_user_service.entity.User;
import com.solutis.projeto.helpdesk_user_service.repository.RoleRepository;
import com.solutis.projeto.helpdesk_user_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(RoleRepository roleRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByNameIgnoreCase("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
        roleRepository.findByNameIgnoreCase("CLIENT")
                .orElseGet(() -> roleRepository.save(new Role("CLIENT")));
        roleRepository.findByNameIgnoreCase("TECHNICIAN")
                .orElseGet(() -> roleRepository.save(new Role("TECHNICIAN")));

        String adminEmail = "admin@helpdesk.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                "Administrador do Sistema",
                adminEmail,
                passwordEncoder.encode("admin123"),
                adminRole
            );
            userRepository.save(admin);
            System.out.println(">>> [SEED] Usuário ADMIN criado: admin@helpdesk.com / admin123");
        }
    }
}

