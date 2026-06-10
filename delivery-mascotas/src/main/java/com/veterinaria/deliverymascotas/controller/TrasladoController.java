package com.veterinaria.deliverymascotas.controller;

import com.veterinaria.deliverymascotas.model.Traslado;
import com.veterinaria.deliverymascotas.service.TrasladoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<CollectionModel<EntityModel<Traslado>>> getAllTraslados() {
        log.info("Obteniendo todos los traslados");
        var traslados = trasladoService.findAll().stream()
                .map(traslado -> EntityModel.of(traslado,
                        linkTo(methodOn(TrasladoController.class).getTrasladoById(traslado.getIdTraslado())).withSelfRel(),
                        linkTo(methodOn(TrasladoController.class).getAllTraslados()).withRel("traslados")))
                .toList();
        var collectionModel = CollectionModel.of(traslados,
                linkTo(methodOn(TrasladoController.class).getAllTraslados()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Traslado>> getTrasladoById(@PathVariable Long id) {
        log.info("Obteniendo traslado con ID: {}", id);
        return trasladoService.findById(id)
                .map(traslado -> EntityModel.of(traslado,
                        linkTo(methodOn(TrasladoController.class).getTrasladoById(id)).withSelfRel(),
                        linkTo(methodOn(TrasladoController.class).getAllTraslados()).withRel("traslados"),
                        linkTo(methodOn(TrasladoController.class).deleteTraslado(id)).withRel("delete")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // por  id http://localhost:8082/api/v1/traslados/2
    @PostMapping
    public ResponseEntity<EntityModel<Traslado>> createTraslado(@Valid @RequestBody Traslado traslado) {
        log.info("Creando nuevo traslado para paciente: {}", traslado.getIdPaciente());
        try {
            Traslado createdTraslado = trasladoService.save(traslado);
            var entityModel = EntityModel.of(createdTraslado,
                    linkTo(methodOn(TrasladoController.class).getTrasladoById(createdTraslado.getIdTraslado())).withSelfRel(),
                    linkTo(methodOn(TrasladoController.class).getAllTraslados()).withRel("traslados"));
            return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al crear traslado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/migrate")
    public ResponseEntity<String> migrateDatabase() {
        log.info("Ejecutando migraciones Flyway manualmente en delivery-mascotas");
        var migrateResult = flyway.migrate();
        return ResponseEntity.ok("Migraciones ejecutadas: " + migrateResult.migrationsExecuted);
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
    public ResponseEntity<EntityModel<Traslado>> updateEstado(@PathVariable Long id, @PathVariable String nuevoEstado) {
        log.info("Actualizando estado de traslado ID: {}", id);
        try {
            Traslado updatedTraslado = trasladoService.updateEstado(id, nuevoEstado);
            var entityModel = EntityModel.of(updatedTraslado,
                    linkTo(methodOn(TrasladoController.class).getTrasladoById(id)).withSelfRel(),
                    linkTo(methodOn(TrasladoController.class).getAllTraslados()).withRel("traslados"));
            return ResponseEntity.ok(entityModel);
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
    public ResponseEntity<EntityModel<Long>> countByEstado(@PathVariable String estado) {
        log.info("Contando traslados con estado: {}", estado);
        long count = trasladoService.countByEstado(estado);
        var entityModel = EntityModel.of(count,
                linkTo(methodOn(TrasladoController.class).countByEstado(estado)).withSelfRel(),
                linkTo(methodOn(TrasladoController.class).getAllTraslados()).withRel("traslados"));
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/estadisticas/trabajador/{idTrabajador}/estado/{estado}")
    public ResponseEntity<EntityModel<Long>> countByTrabajadorAndEstado(@PathVariable Long idTrabajador,
                                                           @PathVariable String estado) {
        log.info("Contando traslados del trabajador {} con estado: {}", idTrabajador, estado);
        long count = trasladoService.countByIdTrabajadorAndEstado(idTrabajador, estado);
        var entityModel = EntityModel.of(count,
                linkTo(methodOn(TrasladoController.class).countByTrabajadorAndEstado(idTrabajador, estado)).withSelfRel(),
                linkTo(methodOn(TrasladoController.class).getAllTraslados()).withRel("traslados"));
        return ResponseEntity.ok(entityModel);
    }
}

//si ve esto profe puse comentarios para guiarme en la evaluacion c: