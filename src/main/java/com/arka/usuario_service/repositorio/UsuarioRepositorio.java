package com.arka.usuario_service.repositorio;

import com.arka.usuario_service.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuarios,Integer> {

    Optional<Usuarios> findByEmail(String email);
    Boolean existsByEmail(String email);



}
