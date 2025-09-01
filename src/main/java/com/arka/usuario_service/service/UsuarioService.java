package com.arka.usuario_service.service;

import com.arka.usuario_service.DTO.AdminDto;
import com.arka.usuario_service.DTO.UserDto;
import com.arka.usuario_service.DTO.UserResponse;
import com.arka.usuario_service.Mapper.UserMapper;
import com.arka.usuario_service.model.UserType;
import com.arka.usuario_service.model.Usuarios;
import com.arka.usuario_service.repositorio.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepositorio repository;
    private final UserMapper mapper;

    public UsuarioService(UsuarioRepositorio repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public UserResponse create(UserDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        validatePassword(dto.getPassword());

        Usuarios newUser = mapper.toEntity(dto);
        if (newUser.getTipo() == UserType.administrador){throw new RuntimeException("You can't create a admin User");}
        Usuarios savedUser = repository.save(newUser);
        return mapper.ToResponse(savedUser);
    }
    public UserResponse createAdmin(AdminDto adminDto) {
        if (repository.existsByEmail(adminDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        validatePassword(adminDto.getPassword());

        Usuarios newUser = mapper.toEntity(adminDto);
        Usuarios savedUser = repository.save(newUser);
        return mapper.ToResponse(savedUser);
    }

    public UserResponse findByEmail(String email) {
        Usuarios user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));
        return mapper.ToResponse(user);
    }



   public List<UserResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::ToResponse)
                .toList();
    }


    public UserResponse delete(Integer id) {
        Usuarios user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        repository.delete(user);
        return mapper.ToResponse(user);
    }

    private void validatePassword(String password) {
        if (password.length() < 8) throw new IllegalArgumentException("Password too short");

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasSpecial = password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));

        if (!hasUpper || !hasSpecial) throw new IllegalArgumentException("Password must have an uppercase letter and a special character");
    }
}
