package com.arka.usuario_service.service;

import com.arka.usuario_service.DTO.AdminDto;
import com.arka.usuario_service.DTO.UserDto;
import com.arka.usuario_service.DTO.UserResponse;
import com.arka.usuario_service.DTO.UserUpdateEmailOrName;
import com.arka.usuario_service.Mapper.UserMapper;
import com.arka.usuario_service.excepcion.UsuarIoNotFound;
import com.arka.usuario_service.model.UserType;
import com.arka.usuario_service.model.Usuarios;
import com.arka.usuario_service.repositorio.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(UserDto dto) {

        if (dto.getNombre().isBlank() || dto.getNombre()==null){
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        validatePassword(dto.getPassword());
        String email= dto.getEmail().trim();
        dto.setEmail(email);
        Usuarios newUser = mapper.toEntity(dto);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
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
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        Usuarios savedUser = repository.save(newUser);
        return mapper.ToResponse(savedUser);
    }

    public UserResponse findByEmail(String email) {
       String email2=email.trim();
    Usuarios user = repository.findByEmail(email2)
            .orElseThrow(() -> new UsuarIoNotFound("Email not found"));
    return mapper.ToResponse(user);
    }

    public List<UserResponse> findAll() {
    return repository.findAll().stream()
            .map(mapper::ToResponse)
            .toList();
    }

    public UserResponse delete(Integer id) {
    Usuarios user = repository.findById(id)
            .orElseThrow(() -> new UsuarIoNotFound("User not found"));
    repository.delete(user);
    return mapper.ToResponse(user);
    }

    public void validatePassword(String password) {
    if (password.length() < 8) throw new IllegalArgumentException("Password too short");

    boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
    boolean hasSpecial = password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));

    if (!hasUpper || !hasSpecial) throw new IllegalArgumentException("Password must have an uppercase letter and a special character");
    }

    public void updatePassword(String email, String password){

        String emailValid=email.trim();
        Usuarios usuarioUpdate=repository.findByEmail(emailValid)
                .orElseThrow(()->new UsuarIoNotFound("User not found"));

        String passwordValid=password.trim();
        validatePassword(passwordValid);
        usuarioUpdate.setPassword(passwordEncoder.encode(password));
        repository.save(usuarioUpdate);
    }

    public UserResponse updateUser(String currentEmail,UserUpdateEmailOrName request) {
        Usuarios user = repository.findByEmail(currentEmail.trim())
                .orElseThrow(() -> new UsuarIoNotFound("User not found"));

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            user.setNombre(request.getNombre());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (repository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
                throw new IllegalArgumentException("Email is already registered");
            }
            user.setEmail(request.getEmail());
        }

        Usuarios updatedUser = repository.save(user);
        return mapper.ToResponse(updatedUser);
    }

    public UserResponse findById(Integer id){
        Usuarios usuario=repository.findById(id)
                .orElseThrow(()->new UsuarIoNotFound("usuario no encontrado"));
        return mapper.ToResponse(usuario);
    }

}
