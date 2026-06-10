package com.veterinaria.personalmedico.service;

import com.veterinaria.personalmedico.dto.PersonalDTO;
import com.veterinaria.personalmedico.model.Personal;
import com.veterinaria.personalmedico.repository.PersonalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para PersonalService")
class PersonalServiceTest {

    private PersonalService personalService;

    @Mock
    private PersonalRepository personalRepository;

    @BeforeEach
    void setUp() {
        personalService = new PersonalService(personalRepository);
    }

    @Test
    @DisplayName("Debe obtener todos los personal médico")
    void testFindAll() {
        // Arrange
        Personal personal1 = Personal.builder()
                .idTrabajador(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .rol("Cirujano")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();

        Personal personal2 = Personal.builder()
                .idTrabajador(2L)
                .nombre("María")
                .apellido("González")
                .rol("Veterinario")
                .rut("87654321-1")
                .correo("maria@vet.com")
                .telefono("+56987654321")
                .direccion("Avenida Principal 200")
                .build();

        when(personalRepository.findAll()).thenReturn(Arrays.asList(personal1, personal2));

        // Act
        List<PersonalDTO> result = personalService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getNombre());
        assertEquals("María", result.get(1).getNombre());
        verify(personalRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener personal por ID")
    void testFindById() {
        // Arrange
        Personal personal = Personal.builder()
                .idTrabajador(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .rol("Cirujano")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();

        when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));

        // Act
        Optional<PersonalDTO> result = personalService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getIdTrabajador());
        assertEquals("Juan", result.get().getNombre());
        verify(personalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe retornar empty si personal no existe")
    void testFindById_NotFound() {
        // Arrange
        when(personalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<PersonalDTO> result = personalService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(personalRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear nuevo personal")
    void testSavePersonal() {
        // Arrange
        PersonalDTO personalDTO = PersonalDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .rol("Cirujano")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();

        Personal personalEntity = Personal.builder()
                .idTrabajador(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .rol("Cirujano")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();

        when(personalRepository.save(any(Personal.class))).thenReturn(personalEntity);

        // Act
        PersonalDTO result = personalService.save(personalDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdTrabajador());
        assertEquals("Juan", result.getNombre());
        verify(personalRepository, times(1)).save(any(Personal.class));
    }

    @Test
    @DisplayName("Debe actualizar personal existente")
    void testUpdatePersonal() {
        // Arrange
        PersonalDTO personalDTO = PersonalDTO.builder()
                .nombre("Juan Actualizado")
                .apellido("Pérez")
                .rol("Cirujano Mayor")
                .rut("12345678-9")
                .correo("juan.actualizado@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();

        Personal personal = Personal.builder()
                .idTrabajador(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .rol("Cirujano")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();

        Personal personalActualizado = Personal.builder()
                .idTrabajador(1L)
                .nombre("Juan Actualizado")
                .apellido("Pérez")
                .rol("Cirujano Mayor")
                .rut("12345678-9")
                .correo("juan.actualizado@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();

        when(personalRepository.findById(1L)).thenReturn(Optional.of(personal));
        when(personalRepository.save(any(Personal.class))).thenReturn(personalActualizado);

        // Act
        PersonalDTO result = personalService.update(1L, personalDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Juan Actualizado", result.getNombre());
        assertEquals("Cirujano Mayor", result.getRol());
        verify(personalRepository, times(1)).findById(1L);
        verify(personalRepository, times(1)).save(any(Personal.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si personal no existe al actualizar")
    void testUpdatePersonal_NotFound() {
        // Arrange
        PersonalDTO personalDTO = PersonalDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .build();

        when(personalRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> personalService.update(999L, personalDTO));

        verify(personalRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe eliminar personal por ID")
    void testDeleteById() {
        // Arrange
        when(personalRepository.existsById(1L)).thenReturn(true);

        // Act
        personalService.deleteById(1L);

        // Assert
        verify(personalRepository, times(1)).existsById(1L);
        verify(personalRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si personal no existe al eliminar")
    void testDeleteById_NotFound() {
        // Arrange
        when(personalRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> personalService.deleteById(999L));

        verify(personalRepository, times(1)).existsById(999L);
        verify(personalRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe verificar si personal existe por ID")
    void testExistsById() {
        // Arrange
        when(personalRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = personalService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(personalRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Debe verificar si personal existe por RUT")
    void testExistsByRut() {
        // Arrange
        when(personalRepository.existsByRut("12345678-9")).thenReturn(true);

        // Act
        boolean result = personalService.existsByRut("12345678-9");

        // Assert
        assertTrue(result);
        verify(personalRepository, times(1)).existsByRut("12345678-9");
    }

    @Test
    @DisplayName("Debe retornar false si RUT no existe")
    void testExistsByRut_NotFound() {
        // Arrange
        when(personalRepository.existsByRut("99999999-9")).thenReturn(false);

        // Act
        boolean result = personalService.existsByRut("99999999-9");

        // Assert
        assertFalse(result);
        verify(personalRepository, times(1)).existsByRut("99999999-9");
    }

    @Test
    @DisplayName("Debe verificar si personal existe por correo")
    void testExistsByCorreo() {
        // Arrange
        when(personalRepository.existsByCorreo("juan@vet.com")).thenReturn(true);

        // Act
        boolean result = personalService.existsByCorreo("juan@vet.com");

        // Assert
        assertTrue(result);
        verify(personalRepository, times(1)).existsByCorreo("juan@vet.com");
    }

    @Test
    @DisplayName("Debe retornar false si correo no existe")
    void testExistsByCorreo_NotFound() {
        // Arrange
        when(personalRepository.existsByCorreo("noexiste@vet.com")).thenReturn(false);

        // Act
        boolean result = personalService.existsByCorreo("noexiste@vet.com");

        // Assert
        assertFalse(result);
        verify(personalRepository, times(1)).existsByCorreo("noexiste@vet.com");
    }
}
