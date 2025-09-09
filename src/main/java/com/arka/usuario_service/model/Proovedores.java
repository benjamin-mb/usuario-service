package com.arka.usuario_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "proveedores")
public class Proovedores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer id;

    @Column(length = 100, nullable = false,unique = true)
    private String nombre;

    @Column(length = 20,nullable = false)
    private String telefono;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    public Proovedores(String nombre, String telefono, String caracteristicas) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.caracteristicas = caracteristicas;
    }
}
