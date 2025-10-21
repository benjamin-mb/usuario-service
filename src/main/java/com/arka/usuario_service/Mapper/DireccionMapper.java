package com.arka.usuario_service.Mapper;

import com.arka.usuario_service.DTO.DtoAddressCreate;
import com.arka.usuario_service.model.Direcciones;
import org.springframework.stereotype.Component;

@Component
public class DireccionMapper {

    public Direcciones toDomain(DtoAddressCreate dto){
        Direcciones direccion=new Direcciones();
        direccion.setDireccion(dto.getDireccion());
        direccion.setDepartamento(dto.getDepartamento());
        direccion.setPrincipal(dto.isPrincipal());
        direccion.setCiudad(dto.getCiudad());
        direccion.setPais(dto.getPais());

        return direccion;
    }
}
