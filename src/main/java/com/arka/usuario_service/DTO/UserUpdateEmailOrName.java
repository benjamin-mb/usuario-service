package com.arka.usuario_service.DTO;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateEmailOrName {

    private String nombre;
    @Email
    private String email;
}
