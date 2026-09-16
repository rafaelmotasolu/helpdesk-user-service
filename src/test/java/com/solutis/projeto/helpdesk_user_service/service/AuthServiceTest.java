package com.solutis.projeto.helpdesk_user_service.service;

import com.solutis.projeto.helpdesk_user_service.dto.AuthRequestDTO;
import com.solutis.projeto.helpdesk_user_service.dto.AuthResponseDTO;
import com.solutis.projeto.helpdesk_user_service.entity.Role;
import com.solutis.projeto.helpdesk_user_service.entity.User;
import com.solutis.projeto.helpdesk_user_service.exception.BusinessException;
import com.solutis.projeto.helpdesk_user_service.repository.UserRepository;
import com.solutis.projeto.helpdesk_user_service.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar token JWT")
    void shouldAuthenticateSuccessfully() {
        AuthRequestDTO request = new AuthRequestDTO("admin@helpdesk.com", "admin123");
        Role adminRole = new Role(1L, "ADMIN");
        User user = new User("Admin", "admin@helpdesk.com", "hashedPass", adminRole);
        user.setId(1L);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(tokenProvider.generateToken(user)).thenReturn("mocked.jwt.token");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals("ADMIN", response.role());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha estiver incorreta")
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        AuthRequestDTO request = new AuthRequestDTO("admin@helpdesk.com", "wrongpassword");
        Role adminRole = new Role(1L, "ADMIN");
        User user = new User("Admin", "admin@helpdesk.com", "hashedPass", adminRole);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));
        assertEquals("Credenciais inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Deve recusar autenticação para usuário inativo")
    void shouldThrowExceptionWhenUserIsInactive() {
        AuthRequestDTO request = new AuthRequestDTO("cliente@helpdesk.com", "senha123");
        Role clientRole = new Role(2L, "CLIENT");
        User user = new User("Cliente", "cliente@helpdesk.com", "hashedPass", clientRole);
        user.setActive(false);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () -> authService.login(request));
        assertEquals("Usuário inativo. Entre em contato com o administrador.", exception.getMessage());
    }
}
