package com.arka.usuario_service.Mapper;

import com.arka.usuario_service.DTO.ProovedorDto;
import com.arka.usuario_service.DTO.ProovedorResponse;
import com.arka.usuario_service.model.Proovedores;
import org.springframework.stereotype.Component;

@Component
public class ProovedorMapper {

    public ProovedorResponse toResponse(Proovedores proovedor){
        if (proovedor == null)return null;
        ProovedorResponse response=  new ProovedorResponse();
        response.setId(proovedor.getId());
        response.setName(proovedor.getNombre());
        return response;
    }

    public Proovedores toEntity(ProovedorDto dto){
        if (dto==null)return null;
        Proovedores proovedores= new Proovedores(
                dto.getNombre(),
                dto.getTelefono(),
                dto.getCaracteristicas()
        );
        return proovedores;
    }
}
