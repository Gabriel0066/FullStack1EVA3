package com.veterinaria.personalmedico.controller;

import com.veterinaria.personalmedico.dto.PersonalDTO;
import com.veterinaria.personalmedico.service.PersonalService;
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
@RequestMapping("/api/v1/personal")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PersonalController {

    private final PersonalService personalService;
    private final Flyway flyway;


    //http://localhost:8081/api/v1/personal

    //create database veterinaria_personal;

    // puerto 8081

    // MRSC orden

    //nuevo personal ejem  {
    //
    //        "rol": "Cirujano",
    //        "nombre": "Marí José",
    //        "apellido": "rera",
    //        "rut": "14321987-1",
    //        "correo": "marijose.rera@veterinaria.com",
    //        "telefono": "+56977776888",
    //        "direccion": "Álvarez 1320, Viña del Mar"
    //    }
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PersonalDTO>>> getAllPersonal() {
        log.info("Obteniendo todo el personal médico (DTO)");
        var personal = personalService.findAll().stream()
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(PersonalController.class).getPersonalById(p.getIdTrabajador())).withSelfRel(),
                        linkTo(methodOn(PersonalController.class).getAllPersonal()).withRel("personal")))
                .toList();
        var collectionModel = CollectionModel.of(personal,
                linkTo(methodOn(PersonalController.class).getAllPersonal()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PersonalDTO>> getPersonalById(@PathVariable Long id) {
        log.info("Obteniendo personal con ID: {}", id);
        return personalService.findById(id)
                .map(p -> EntityModel.of(p,
                        linkTo(methodOn(PersonalController.class).getPersonalById(id)).withSelfRel(),
                        linkTo(methodOn(PersonalController.class).getAllPersonal()).withRel("personal"),
                        linkTo(methodOn(PersonalController.class).deletePersonal(id)).withRel("delete")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
// get http://localhost:8081/api/v1/personal/4
    @PostMapping
    public ResponseEntity<EntityModel<PersonalDTO>> createPersonal(@Valid @RequestBody PersonalDTO personalDTO) {
        log.info("Creando nuevo personal médico desde DTO: {}", personalDTO.getNombre());
        try {
            PersonalDTO createdPersonal = personalService.save(personalDTO);
            var entityModel = EntityModel.of(createdPersonal,
                    linkTo(methodOn(PersonalController.class).getPersonalById(createdPersonal.getIdTrabajador())).withSelfRel(),
                    linkTo(methodOn(PersonalController.class).getAllPersonal()).withRel("personal"));
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/api/v1/personal/" + createdPersonal.getIdTrabajador())
                    .body(entityModel);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear personal: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/migrate")
    public ResponseEntity<String> migrateDatabase() {
        log.info("Ejecutando migraciones Flyway manualmente en personal-medico");
        var migrateResult = flyway.migrate();
        return ResponseEntity.ok("Migraciones ejecutadas: " + migrateResult.migrationsExecuted);
    }

    //nuevo personal post {
    //    "idTrabajador": 5,
    //    "rol": "Cirujano",
    //    "nombre": "Daniel",
    //    "apellido": "Mars",
    //    "rut": "14321987-9",
    //    "correo": "daniel.mars@veterinaria.com",
    //    "telefono": "+56987978888",
    //    "direccion": "Calle argentina"
    //}

 // si creo un nuevo traslado con un idtrabajador que no existe no sea crea
    //pero si lo hago con un trabajador existente como por ejemplo el nuevo trabajador 5 funciona



    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PersonalDTO>> updatePersonal(@PathVariable Long id, @Valid @RequestBody PersonalDTO personalDetailsDTO) {
        log.info("Actualizando personal con ID: {}", id);
        try {
            PersonalDTO updatedPersonal = personalService.update(id, personalDetailsDTO);
            var entityModel = EntityModel.of(updatedPersonal,
                    linkTo(methodOn(PersonalController.class).getPersonalById(id)).withSelfRel(),
                    linkTo(methodOn(PersonalController.class).getAllPersonal()).withRel("personal"));
            return ResponseEntity.ok(entityModel);
        } catch (RuntimeException e) {
            log.error("Error al actualizar personal {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonal(@PathVariable Long id) {
        log.info("Eliminando personal con ID: {}", id);
        try {
            personalService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error al eliminar personal {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/id/{id}")
    public ResponseEntity<EntityModel<Boolean>> existsById(@PathVariable Long id) {
        log.info("Verificando si existe personal con ID: {}", id);
        boolean exists = personalService.existsById(id);
        var entityModel = EntityModel.of(exists,
                linkTo(methodOn(PersonalController.class).existsById(id)).withSelfRel(),
                linkTo(methodOn(PersonalController.class).getAllPersonal()).withRel("personal"));
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/exists/rut/{rut}")
    public ResponseEntity<EntityModel<Boolean>> existsByRut(@PathVariable String rut) {
        log.info("Verificando si existe personal con RUT: {}", rut);
        boolean exists = personalService.existsByRut(rut);
        var entityModel = EntityModel.of(exists,
                linkTo(methodOn(PersonalController.class).existsByRut(rut)).withSelfRel(),
                linkTo(methodOn(PersonalController.class).getAllPersonal()).withRel("personal"));
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/exists/correo/{correo}")
    public ResponseEntity<EntityModel<Boolean>> existsByCorreo(@PathVariable String correo) {
        log.info("Verificando si existe personal con correo: {}", correo);
        boolean exists = personalService.existsByCorreo(correo);
        var entityModel = EntityModel.of(exists,
                linkTo(methodOn(PersonalController.class).existsByCorreo(correo)).withSelfRel(),
                linkTo(methodOn(PersonalController.class).getAllPersonal()).withRel("personal"));
        return ResponseEntity.ok(entityModel);
    }
}