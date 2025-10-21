package com.arka.usuario_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direcciones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Direcciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario",nullable = false)
    @JsonBackReference
    private Usuarios usuario;
    private String ciudad;
    private String pais;
    private String departamento;
    private String direccion;
    private boolean principal=false;

    public Direcciones(Usuarios usuario, String ciudad,String pais, String departamento, String direccion, boolean principal) {
        this.usuario = usuario;
        this.ciudad=ciudad;
        this.pais = pais;
        this.departamento = departamento;
        this.direccion = direccion;
        this.principal = principal;
    }
}
