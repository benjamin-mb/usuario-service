package com.arka.usuario_service.repositorio;

import com.arka.usuario_service.model.Direcciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DireccionesRepository extends JpaRepository<Direcciones, Integer> {
    Optional<Direcciones>findByUsuario_IdAndPrincipal(Integer idUsuario, boolean principal);
    Optional<Direcciones>findByUsuario_IdAndId(Integer idUsuario, Integer id);
    List<Direcciones>findAllByUsuario_Id(Integer idUsuario);
    @Modifying
    @Query("UPDATE Direcciones d SET d.principal = false WHERE d.usuario.id = :idUsuario")
    void removeAllPrincipalFlags(Integer idUsuario);
}
