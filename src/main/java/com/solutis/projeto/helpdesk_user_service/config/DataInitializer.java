package com.solutis.projeto.helpdesk_user_service.config;

import com.solutis.projeto.helpdesk_user_service.entity.Role;
import com.solutis.projeto.helpdesk_user_service.entity.User;
import com.solutis.projeto.helpdesk_user_service.repository.RoleRepository;
import com.solutis.projeto.helpdesk_user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = getOrCreateRole("ADMIN");
        Role techRole = getOrCreateRole("TECHNICIAN");
        Role clientRole = getOrCreateRole("CLIENT");

        // Admin default
        getOrCreateUser("Administrador do Sistema", "admin@helpdesk.com", "admin123", adminRole);

        // Técnicos
        getOrCreateUser("Carlos Silva", "carlos.silva@helpdesk.com", "admin123", techRole);
        getOrCreateUser("Mariana Souza", "mariana.souza@helpdesk.com", "admin123", techRole);
        getOrCreateUser("Roberto Lima", "roberto.lima@helpdesk.com", "admin123", techRole);

        // Clientes
        getOrCreateUser("Ana Pereira", "ana.pereira@empresa.com", "admin123", clientRole);
        getOrCreateUser("Lucas Ferreira", "lucas.ferreira@empresa.com", "admin123", clientRole);
        getOrCreateUser("Beatriz Santos", "beatriz.santos@empresa.com", "admin123", clientRole);

        log.info("População inicial de usuários e papéis concluída com sucesso.");
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByNameIgnoreCase(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName.toUpperCase())));
    }

    private void getOrCreateUser(String name, String email, String rawPassword, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User(name, email, passwordEncoder.encode(rawPassword), role);
            userRepository.save(user);
            log.info("Usuário inicial criado: {} ({}) - Perfil: {}", name, email, role.getName());
        }
    }
}

