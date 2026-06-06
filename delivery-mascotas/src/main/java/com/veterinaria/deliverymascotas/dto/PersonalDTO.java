package com.veterinaria.deliverymascotas.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalDTO {
    
    private Long idTrabajador;
    private String rol;
    private String nombre;
    private String apellido;
    private String rut;
    private String correo;
    private String telefono;
    private String direccion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
