package com.veterinaria.deliverymascotas.controller;

import com.veterinaria.deliverymascotas.model.Traslado;
import com.veterinaria.deliverymascotas.service.TrasladoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/traslados")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class TrasladoController {

    private final TrasladoService trasladoService;
    private final Flyway flyway;


    //http://localhost:8082/api/v1/traslados

    //create database veterinaria_delivery;

    // purto 8082

    //MRSC orden

    //end points
    //ejemplo trasnlado crear
    //{
    //        "idTraslado":6,
    //        "idPaciente": 109,
    //        "idTrabajador": 1,
    //        "direccionHogar": "Av. Uno Oriente 340, Viña del Mar",
    //        "horaRecogida": "08:30",
    //        "estado": "CANCELADO"
    //    }

    //http://localhost:8082/api/v1/traslados
    @GetMapping
    public ResponseEntity<List<Traslado>> getAllTraslados() {
        log.info("Obteniendo todos los traslados");
        List<Traslado> traslados = trasladoService.findAll();
        return ResponseEntity.ok(traslados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Traslado> getTrasladoById(@PathVariable Long id) {
        log.info("Obteniendo traslado con ID: {}", id);
        return trasladoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // por  id http://localhost:8082/api/v1/traslados/2
    @PostMapping
    public ResponseEntity<Traslado> createTraslado(@Valid @RequestBody Traslado traslado) {
        log.info("Creando nuevo traslado para paciente: {}", traslado.getIdPaciente());
        try {
            Traslado createdTraslado = trasladoService.save(traslado);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTraslado);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al crear traslado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/migrate")
    public ResponseEntity<String> migrateDatabase() {
        log.info("Ejecutando migraciones Flyway manualmente en delivery-mascotas");
        int migrations = flyway.migrate();
        return ResponseEntity.ok("Migraciones ejecutadas: " + migrations);
    }

    //{
    //
    //        "idPaciente": 106,
    //        "idTrabajador": 2,
    //        "direccionHogar": "Canal kirke 450",
    //        "horaRecogida": "09:30",
    //        "estado": "CANCELADO"
    //    } nuevo dato





    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<Traslado> updateEstado(@PathVariable Long id, @PathVariable String nuevoEstado) {
        log.info("Actualizando estado de traslado ID: {}", id);
        try {
            Traslado updatedTraslado = trasladoService.updateEstado(id, nuevoEstado);
            return ResponseEntity.ok(updatedTraslado);
        } catch (RuntimeException e) {
            log.error("Error al actualizar estado del traslado {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    //http://localhost:8082/api/v1/traslados/1/estado/CANCELADO nuevo estado

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraslado(@PathVariable Long id) {
        log.info("Eliminando traslado con ID: {}", id);
        try {
            trasladoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error al eliminar traslado {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
//cn idTraslado http://localhost:8082/api/v1/traslados/6
    @GetMapping("/estadisticas/estado/{estado}")
    public ResponseEntity<Long> countByEstado(@PathVariable String estado) {
        log.info("Contando traslados con estado: {}", estado);
        long count = trasladoService.countByEstado(estado);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/estadisticas/trabajador/{idTrabajador}/estado/{estado}")
    public ResponseEntity<Long> countByTrabajadorAndEstado(@PathVariable Long idTrabajador,
                                                           @PathVariable String estado) {
        log.info("Contando traslados del trabajador {} con estado: {}", idTrabajador, estado);
        long count = trasladoService.countByIdTrabajadorAndEstado(idTrabajador, estado);
        return ResponseEntity.ok(count);
    }
}

//si ve esto profe puse comentarios para guiarme en la evaluacion c: