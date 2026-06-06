package com.veterinaria.personalmedico.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "personal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trabajador")
    private Long idTrabajador;
    
    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 50, message = "El rol no puede exceder 50 caracteres")
    @Column(name = "rol", nullable = false, length = 50)
    private String rol;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
    
    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "El RUT debe tener formato válido (ej: 12345678-9)")
    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;
    
    @Email(message = "El correo debe tener formato válido")
    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "El teléfono debe tener entre 8 y 15 dígitos")
    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;
}
