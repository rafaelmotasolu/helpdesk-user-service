package com.solutis.projeto.helpdesk_user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutis.projeto.helpdesk_user_service.dto.UserCreateDTO;
import com.solutis.projeto.helpdesk_user_service.dto.UserResponseDTO;
import com.solutis.projeto.helpdesk_user_service.entity.Role;
import com.solutis.projeto.helpdesk_user_service.security.JwtTokenProvider;
import com.solutis.projeto.helpdesk_user_service.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.solutis.projeto.helpdesk_user_service.config.SecurityConfig;
import com.solutis.projeto.helpdesk_user_service.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Import;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /users - ADMIN deve criar usuário e retornar 201 Created")
    void adminShouldCreateUserSuccessfully() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("Joao Dev", "joao@helpdesk.com", "123456", Role.CLIENT);
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "Joao Dev", "joao@helpdesk.com", Role.CLIENT, true, LocalDateTime.now());

        when(userService.create(any(UserCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("joao@helpdesk.com"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("POST /users - CLIENT não deve ter autorização e receber 403 Forbidden")
    void clientCannotCreateUser() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("Joao Dev", "joao@helpdesk.com", "123456", Role.CLIENT);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /users - Deve retornar 400 Bad Request se campos obrigatórios forem inválidos")
    void shouldReturnBadRequestOnInvalidPayload() throws Exception {
        // Envio com email inválido e senha curta
        UserCreateDTO invalidDto = new UserCreateDTO("", "email-invalido", "123", null);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    @DisplayName("GET /users - Técnico deve conseguir listar usuários e receber 200 OK")
    void technicianCanListUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(
                new UserResponseDTO(1L, "Admin", "admin@helpdesk.com", Role.ADMIN, true, LocalDateTime.now())
        ));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /users/{id} - ADMIN deve inativar usuário e retornar 204 No Content")
    void adminCanInactivateUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L).with(csrf()))
                .andExpect(status().isNoContent());
    }
}