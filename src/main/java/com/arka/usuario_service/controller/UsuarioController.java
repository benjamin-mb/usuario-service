package com.arka.usuario_service.controller;

import com.arka.usuario_service.DTO.*;
import com.arka.usuario_service.model.Usuarios;
import com.arka.usuario_service.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")

public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public String health(){
        return "health";
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsuarios() {
        List<UserResponse> usuarios = service.findAll();
        return ResponseEntity.ok(usuarios);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUsuarioById(@PathVariable Integer id) {
        UserResponse usuario = service.delete(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUsuarioByEmail(@PathVariable String email) {
        UserResponse usuario = service.findByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUsuario(@RequestBody UserDto userDto) {
        UserResponse nuevoUsuario = service.create(userDto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/admin")
    public ResponseEntity<UserResponse>createAdmin(@RequestBody AdminDto adminDto){
        UserResponse nuevoUsuario=service.createAdmin(adminDto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUsuario(@PathVariable Integer id) {
        UserResponse eliminado = service.delete(id);
        return ResponseEntity.ok(eliminado);
    }

    @PutMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdateUserRequest request) {
        service.updatePassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok("Password updated successfully");
    }

    public ResponseEntity<UserResponse> updateUser(
            @RequestParam String currentEmail,
            @RequestBody UserUpdateEmailOrName request) {
        return ResponseEntity.ok(service.updateUser(currentEmail, request));
    }
}
