package com.veterinaria.deliverymascotas.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalTime;

@Entity
@Table(name = "traslado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Traslado {
    //MRSC orden
     //  validaciones con el uso de entity (jpa)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_traslado")
    private Long idTraslado;

    @NotNull(message = "El ID del paciente es obligatorio")
    @Column(name = "id_paciente", nullable = false)
    private Long idPaciente;
    
    @NotNull(message = "El ID del trabajador es obligatorio")
    @Column(name = "id_trabajador", nullable = false)
    private Long idTrabajador;
    
    @NotBlank(message = "La dirección de hogar es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(name = "direccion_hogar", nullable = false, length = 200)
    private String direccionHogar;
    
    @NotNull(message = "La hora de recogida es obligatoria")
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "hora_recogida", nullable = false)
    private LocalTime horaRecogida;
    
    @NotBlank(message = "El estado es obligatorio")
    @Size(max = 20, message = "El estado no puede exceder 20 caracteres")
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
//no olvidar que usa anotaciones de validacion y jpa