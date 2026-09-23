package com.solutis.projeto.helpdesk_user_service.service;

import com.solutis.projeto.helpdesk_user_service.dto.UserCreateDTO;
import com.solutis.projeto.helpdesk_user_service.dto.UserResponseDTO;
import com.solutis.projeto.helpdesk_user_service.entity.Role;
import com.solutis.projeto.helpdesk_user_service.entity.User;
import com.solutis.projeto.helpdesk_user_service.exception.BusinessException;
import com.solutis.projeto.helpdesk_user_service.exception.ResourceNotFoundException;
import com.solutis.projeto.helpdesk_user_service.repository.RoleRepository;
import com.solutis.projeto.helpdesk_user_service.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve criar um usuário com sucesso quando a role existir no banco e codificar a senha")
    void shouldCreateUserSuccessfully() {
        UserCreateDTO dto = new UserCreateDTO("Carlos Silva", "carlos@helpdesk.com", "senha123", "TECHNICIAN");
        Role technicianRole = new Role(2L, "TECHNICIAN");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(roleRepository.findByNameIgnoreCase("TECHNICIAN")).thenReturn(Optional.of(technicianRole));
        when(passwordEncoder.encode(dto.password())).thenReturn("encodedPassword123");

        User savedUser = new User(dto.name(), dto.email(), "encodedPassword123", technicianRole);
        savedUser.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO response = userService.create(dto);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(dto.name(), response.name());
        assertEquals(dto.email(), response.email());
        assertEquals("TECHNICIAN", response.role());
        assertTrue(response.active());

        verify(roleRepository).findByNameIgnoreCase("TECHNICIAN");
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando a role informada não existir na base de dados")
    void shouldThrowExceptionWhenRoleDoesNotExist() {
        UserCreateDTO dto = new UserCreateDTO("Carlos Silva", "carlos@helpdesk.com", "senha123", "INVALID_ROLE");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(roleRepository.findByNameIgnoreCase("INVALID_ROLE")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(dto));
        assertEquals("O perfil (role) 'INVALID_ROLE' não existe na base de dados.", exception.getMessage());

        verify(roleRepository).findByNameIgnoreCase("INVALID_ROLE");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando o e-mail já estiver cadastrado")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserCreateDTO dto = new UserCreateDTO("Carlos Silva", "carlos@helpdesk.com", "senha123", "TECHNICIAN");

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> userService.create(dto));
        assertEquals("O e-mail informado já está cadastrado.", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(roleRepository, never()).findByNameIgnoreCase(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void shouldThrowExceptionWhenUserNotFoundById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    @DisplayName("Deve inativar logicamente o usuário com sucesso")
    void shouldInactivateUserSuccessfully() {
        User user = new User("Ana Santos", "ana@helpdesk.com", "pass", new Role(1L, "CLIENT"));
        user.setId(2L);
        user.setActive(true);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.inactivate(2L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Deve alterar a senha do usuário com hash BCrypt com sucesso")
    void shouldUpdateUserPasswordSuccessfully() {
        User user = new User("Ana Santos", "ana@helpdesk.com", "oldHash", new Role(1L, "CLIENT"));
        user.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("newEncodedHash");

        userService.updatePassword(2L, new com.solutis.projeto.helpdesk_user_service.dto.UserPasswordUpdateDTO("novaSenha123"));

        assertEquals("newEncodedHash", user.getPassword());
        verify(userRepository).save(user);
    }
}