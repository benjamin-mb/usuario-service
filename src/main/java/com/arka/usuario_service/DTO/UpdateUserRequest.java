package com.arka.usuario_service.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Email(message = "Email is not valid")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String newPassword;
}
