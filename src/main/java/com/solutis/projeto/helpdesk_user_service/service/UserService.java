package com.solutis.projeto.helpdesk_user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.solutis.projeto.helpdesk_user_service.dto.UserCreateDTO;
import com.solutis.projeto.helpdesk_user_service.dto.UserResponseDTO;
import com.solutis.projeto.helpdesk_user_service.dto.UserUpdateDTO;
import com.solutis.projeto.helpdesk_user_service.entity.User;
import com.solutis.projeto.helpdesk_user_service.exception.BusinessException;
import com.solutis.projeto.helpdesk_user_service.exception.ResourceNotFoundException;
import com.solutis.projeto.helpdesk_user_service.repository.UserRepository;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO create(UserCreateDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new BusinessException("O e-mail informado já está cadastrado.");
        }

        User user = new User(
                dto.name(),
                dto.email(),
                passwordEncoder.encode(dto.password()),
                dto.role()
        );

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));

        if (userRepository.existsByEmailAndIdNot(dto.email(), id)) {
            throw new BusinessException("O e-mail informado já pertence a outro usuário.");
        }

        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setRole(dto.role());

        return UserResponseDTO.fromEntity(userRepository.save(user));
    }

    @Transactional
    public void inactivate(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o ID: " + id));
        user.setActive(false);
        userRepository.save(user);
    }
}
