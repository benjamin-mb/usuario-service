package com.arka.usuario_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoAddressCreate {
    private String pais;
    private String ciudad;
    private String departamento;
    private String direccion;
    private boolean principal;
}
