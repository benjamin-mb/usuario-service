package com.arka.usuario_service.Mapper;

import com.arka.usuario_service.DTO.AdminDto;
import com.arka.usuario_service.DTO.UserDto;
import com.arka.usuario_service.DTO.UserResponse;
import com.arka.usuario_service.model.Usuarios;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Usuarios toEntity(UserDto userDto){
        if (userDto == null)return null;
        Usuarios usuario=new Usuarios(
        userDto.getNombre(),
        userDto.getEmail(),
        userDto.getPassword()
        );
        return usuario;
    }

    public Usuarios toEntity(AdminDto adminDto){
        if (adminDto == null)return null;
        Usuarios usuario=new Usuarios(
                adminDto.getNombre(),
                adminDto.getEmail(),
                adminDto.getPassword(),
                adminDto.getTipo()
        );
        return usuario;
    }



    public UserResponse ToResponse(Usuarios usuario){
        if (usuario== null)return null;
        UserResponse userResponse=new UserResponse(
                usuario.getNombre(),
                usuario.getEmail()
        );
        return userResponse;
    }


}
