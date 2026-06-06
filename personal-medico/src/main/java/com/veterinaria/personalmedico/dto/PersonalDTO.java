package com.veterinaria.personalmedico.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDTO {
    
    private Long idTrabajador;
    
    @NotBlank(message = "El rol es obligatorio")
    @Size(max = 50, message = "El rol no puede exceder 50 caracteres")
    private String rol;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String apellido;
    
    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "El RUT debe tener formato válido (ej: 12345678-9)")
    private String rut;
    
    @Email(message = "El correo debe tener formato válido")
    @NotBlank(message = "El correo es obligatorio")
    @Size(max = 100, message = "El correo no puede exceder 100 caracteres")
    private String correo;
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "El teléfono debe tener entre 8 y 15 dígitos")
    private String telefono;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;
}
