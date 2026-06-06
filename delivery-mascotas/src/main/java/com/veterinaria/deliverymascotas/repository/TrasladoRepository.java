package com.veterinaria.deliverymascotas.repository;

import com.veterinaria.deliverymascotas.model.Traslado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrasladoRepository extends JpaRepository<Traslado, Long> {

    //consultas automaticas y manuales
    
    List<Traslado> findByIdTrabajador(Long idTrabajador);
    
    List<Traslado> findByIdPaciente(Long idPaciente);
    
    List<Traslado> findByEstado(String estado);
    
    @Query("SELECT t FROM Traslado t WHERE t.estado = :estado AND t.idTrabajador = :idTrabajador")
    List<Traslado> findByEstadoAndIdTrabajador(@Param("estado") String estado, 
                                               @Param("idTrabajador") Long idTrabajador);
    
    @Query("SELECT t FROM Traslado t WHERE t.horaRecogida BETWEEN :fechaInicio AND :fechaFin")
    List<Traslado> findByHoraRecogidaBetween(@Param("fechaInicio") LocalTime fechaInicio,
                                              @Param("fechaFin") LocalTime fechaFin);
    
    @Query("SELECT t FROM Traslado t WHERE t.estado = :estado AND " +
           "t.horaRecogida BETWEEN :fechaInicio AND :fechaFin")
    List<Traslado> findByEstadoAndHoraRecogidaBetween(@Param("estado") String estado,
                                                       @Param("fechaInicio") LocalTime fechaInicio,
                                                       @Param("fechaFin") LocalTime fechaFin);
    
    @Query("SELECT t FROM Traslado t WHERE t.idTrabajador = :idTrabajador AND " +
           "t.horaRecogida BETWEEN :fechaInicio AND :fechaFin")
    List<Traslado> findByIdTrabajadorAndHoraRecogidaBetween(@Param("idTrabajador") Long idTrabajador,
                                                             @Param("fechaInicio") LocalTime fechaInicio,
                                                             @Param("fechaFin") LocalTime fechaFin);
    
    @Query("SELECT COUNT(t) FROM Traslado t WHERE t.estado = :estado")
    long countByEstado(@Param("estado") String estado);
    
    @Query("SELECT COUNT(t) FROM Traslado t WHERE t.idTrabajador = :idTrabajador AND t.estado = :estado")
    long countByIdTrabajadorAndEstado(@Param("idTrabajador") Long idTrabajador, 
                                      @Param("estado") String estado);
}
//manuales

//el uso de @query es para consultas personalizadas