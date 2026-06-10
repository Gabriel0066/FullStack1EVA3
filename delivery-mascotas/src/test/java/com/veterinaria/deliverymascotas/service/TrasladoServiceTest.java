package com.veterinaria.deliverymascotas.service;

import com.veterinaria.deliverymascotas.client.PersonalClient;
import com.veterinaria.deliverymascotas.model.Traslado;
import com.veterinaria.deliverymascotas.repository.TrasladoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para TrasladoService")
class TrasladoServiceTest {

    private TrasladoService trasladoService;

    @Mock
    private TrasladoRepository trasladoRepository;

    @Mock
    private PersonalClient personalClient;

    @BeforeEach
    void setUp() {
        trasladoService = new TrasladoService(trasladoRepository, personalClient);
    }

    @Test
    @DisplayName("Debe obtener todos los traslados")
    void testFindAll() {
        // Arrange
        Traslado traslado1 = Traslado.builder()
                .idTraslado(1L)
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE")
                .build();

        Traslado traslado2 = Traslado.builder()
                .idTraslado(2L)
                .idPaciente(101L)
                .idTrabajador(2L)
                .direccionHogar("Avenida Secundaria 200")
                .horaRecogida(LocalTime.of(10, 30))
                .estado("COMPLETADO")
                .build();

        when(trasladoRepository.findAll()).thenReturn(Arrays.asList(traslado1, traslado2));

        // Act
        List<Traslado> result = trasladoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("PENDIENTE", result.get(0).getEstado());
        verify(trasladoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener traslado por ID")
    void testFindById() {
        // Arrange
        Traslado traslado = Traslado.builder()
                .idTraslado(1L)
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE")
                .build();

        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(traslado));

        // Act
        Optional<Traslado> result = trasladoService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getIdTraslado());
        assertEquals(100L, result.get().getIdPaciente());
        verify(trasladoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe retornar empty si traslado no existe")
    void testFindByIdNotFound() {
        // Arrange
        when(trasladoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Traslado> result = trasladoService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(trasladoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe crear traslado si trabajador existe")
    void testSaveTraslado_WhenTrabajadorExists() {
        // Arrange
        Traslado traslado = Traslado.builder()
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado(null)
                .build();

        Traslado savedTraslado = Traslado.builder()
                .idTraslado(1L)
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE")
                .build();

        when(personalClient.existsById(1L)).thenReturn(true);
        when(trasladoRepository.save(any(Traslado.class))).thenReturn(savedTraslado);

        // Act
        Traslado result = trasladoService.save(traslado);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdTraslado());
        assertEquals("PENDIENTE", result.getEstado());
        verify(personalClient, times(1)).existsById(1L);
        verify(trasladoRepository, times(1)).save(any(Traslado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si trabajador no existe")
    void testSaveTraslado_WhenTrabajadorDoesNotExist() {
        // Arrange
        Traslado traslado = Traslado.builder()
                .idPaciente(100L)
                .idTrabajador(999L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .build();

        when(personalClient.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trasladoService.save(traslado));

        assertTrue(exception.getMessage().contains("ID del trabajador médico no existe"));
        verify(personalClient, times(1)).existsById(999L);
        verify(trasladoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe establecer estado PENDIENTE por defecto")
    void testSaveTraslado_SetEstadoPendienteByDefault() {
        // Arrange
        Traslado traslado = Traslado.builder()
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado(null)
                .build();

        Traslado savedTraslado = Traslado.builder()
                .idTraslado(1L)
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE")
                .build();

        when(personalClient.existsById(1L)).thenReturn(true);
        when(trasladoRepository.save(any(Traslado.class))).thenReturn(savedTraslado);

        // Act
        Traslado result = trasladoService.save(traslado);

        // Assert
        assertEquals("PENDIENTE", result.getEstado());
    }

    @Test
    @DisplayName("Debe actualizar estado de traslado")
    void testUpdateEstado() {
        // Arrange
        Traslado traslado = Traslado.builder()
                .idTraslado(1L)
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE")
                .build();

        Traslado updatedTraslado = Traslado.builder()
                .idTraslado(1L)
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("COMPLETADO")
                .build();

        when(trasladoRepository.findById(1L)).thenReturn(Optional.of(traslado));
        when(trasladoRepository.save(any(Traslado.class))).thenReturn(updatedTraslado);

        // Act
        Traslado result = trasladoService.updateEstado(1L, "COMPLETADO");

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETADO", result.getEstado());
        verify(trasladoRepository, times(1)).findById(1L);
        verify(trasladoRepository, times(1)).save(any(Traslado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si traslado no existe al actualizar")
    void testUpdateEstado_WhenTrasladoNotFound() {
        // Arrange
        when(trasladoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trasladoService.updateEstado(999L, "COMPLETADO"));

        assertTrue(exception.getMessage().contains("no encontrado"));
        verify(trasladoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe eliminar traslado por ID")
    void testDeleteById() {
        // Arrange
        when(trasladoRepository.existsById(1L)).thenReturn(true);

        // Act
        trasladoService.deleteById(1L);

        // Assert
        verify(trasladoRepository, times(1)).existsById(1L);
        verify(trasladoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción si traslado no existe al eliminar")
    void testDeleteById_WhenTrasladoNotFound() {
        // Arrange
        when(trasladoRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> trasladoService.deleteById(999L));

        assertTrue(exception.getMessage().contains("no encontrado"));
        verify(trasladoRepository, times(1)).existsById(999L);
        verify(trasladoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe contar traslados por estado")
    void testCountByEstado() {
        // Arrange
        when(trasladoRepository.countByEstado("PENDIENTE")).thenReturn(5L);

        // Act
        long result = trasladoService.countByEstado("PENDIENTE");

        // Assert
        assertEquals(5L, result);
        verify(trasladoRepository, times(1)).countByEstado("PENDIENTE");
    }

    @Test
    @DisplayName("Debe contar traslados por trabajador y estado")
    void testCountByIdTrabajadorAndEstado() {
        // Arrange
        when(trasladoRepository.countByIdTrabajadorAndEstado(1L, "COMPLETADO")).thenReturn(3L);

        // Act
        long result = trasladoService.countByIdTrabajadorAndEstado(1L, "COMPLETADO");

        // Assert
        assertEquals(3L, result);
        verify(trasladoRepository, times(1)).countByIdTrabajadorAndEstado(1L, "COMPLETADO");
    }

    @Test
    @DisplayName("Debe verificar si traslado existe")
    void testExistsById() {
        // Arrange
        when(trasladoRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = trasladoService.existsById(1L);

        // Assert
        assertTrue(result);
        verify(trasladoRepository, times(1)).existsById(1L);
    }
}
