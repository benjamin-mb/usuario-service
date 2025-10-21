package com.arka.usuario_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    @Email(message = "please provide a valid email")
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('cliente','administrador')", nullable = true)
    private UserType tipo;

    @OneToMany(mappedBy = "usuario", cascade=CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Direcciones> direcciones;

    public Usuarios(String nombre, String email, String password) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipo= UserType.cliente;
    }

    public Usuarios(String nombre, String email, String password, UserType tipo) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.tipo = tipo;
    }
}
