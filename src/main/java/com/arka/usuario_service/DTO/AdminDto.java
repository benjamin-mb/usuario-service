package com.arka.usuario_service.DTO;

import com.arka.usuario_service.model.UserType;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDto {
    private String nombre;
    @Email
    private String email;
    private String password;
    private UserType tipo;

    public AdminDto(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipo = UserType.administrador;
    }
}
