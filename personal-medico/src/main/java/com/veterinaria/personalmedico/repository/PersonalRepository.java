package com.veterinaria.personalmedico.repository;

import com.veterinaria.personalmedico.model.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {
    
    Optional<Personal> findByRut(String rut);
    
    Optional<Personal> findByCorreo(String correo);
    
    List<Personal> findByRol(String rol);
    
    @Query("SELECT p FROM Personal p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
           "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))")
    List<Personal> findByNombreOrApellidoContaining(@Param("nombre") String nombre, 
                                                   @Param("apellido") String apellido);
    
    @Query("SELECT p FROM Personal p WHERE " +
           "LOWER(p.rol) = LOWER(:rol) AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
           "LOWER(p.apellido) LIKE LOWER(CONCAT('%', :termino, '%')))")
    List<Personal> findByRolAndNombreOrApellidoContaining(@Param("rol") String rol,
                                                         @Param("termino") String termino);
    
    boolean existsByRut(String rut);
    
    boolean existsByCorreo(String correo);
}
