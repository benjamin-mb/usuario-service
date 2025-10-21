package com.arka.usuario_service.repositorio;

import com.arka.usuario_service.model.Proovedores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProovedorRepository extends JpaRepository<Proovedores,Integer> {
    Boolean existsByNombre(String nombre);
    boolean existsById(Integer id);

}
