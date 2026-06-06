package com.veterinaria.deliverymascotas.service;

import com.veterinaria.deliverymascotas.client.PersonalClient; // Cliente OpenFeign para validar personal remoto
import com.veterinaria.deliverymascotas.model.Traslado;
import com.veterinaria.deliverymascotas.repository.TrasladoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrasladoService {
//capa del negocio y esta protegida con @transacional
    private final TrasladoRepository trasladoRepository;
    private final PersonalClient personalClient;

    public List<Traslado> findAll() {
        log.info("Buscando todos los traslados");
        return trasladoRepository.findAll();
    }

    public Optional<Traslado> findById(Long id) {
        log.info("Buscando traslado con ID: {}", id);
        return trasladoRepository.findById(id);
    }

    public Traslado save(Traslado traslado) {
        log.info("Guardando nuevo traslado - Ejecutando validación inter-servicio");

        // Llamada al cliente Feign para validar que el trabajador médico exista en el servicio personal-medico
        boolean existeTrabajador = personalClient.existsById(traslado.getIdTrabajador());

        if (!existeTrabajador) {
            log.error("Error: El idTrabajador {} no existe en el sistema de Personal Médico", traslado.getIdTrabajador());
            throw new IllegalArgumentException("No se puede registrar el traslado. El ID del trabajador médico no existe.");
        }
//si existe(no existe tirar un error)
        if (traslado.getEstado() == null || traslado.getEstado().isEmpty()) {
            traslado.setEstado("PENDIENTE");
        }
        return trasladoRepository.save(traslado);
    }
//si existe set estado
    public Traslado updateEstado(Long id, String nuevoEstado) {
        log.info("Actualizando estado del traslado {} a: {}", id, nuevoEstado);

        Traslado traslado = trasladoRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("No se encontró traslado con ID: {}", id);
                    return new IllegalArgumentException("Traslado no encontrado con ID: " + id);
                });

        traslado.setEstado(nuevoEstado);
        return trasladoRepository.save(traslado);
    }

    public void deleteById(Long id) {
        log.info("Eliminando traslado con ID: {}", id);
        if (!trasladoRepository.existsById(id)) {
            log.error("No se encontró traslado con ID: {}", id);
            throw new IllegalArgumentException("Traslado no encontrado con ID: " + id);
        }
        trasladoRepository.deleteById(id);
    }

    public long countByEstado(String estado) {
        return trasladoRepository.countByEstado(estado);
    }

    public long countByIdTrabajadorAndEstado(Long idTrabajador, String estado) {
        return trasladoRepository.countByIdTrabajadorAndEstado(idTrabajador, estado);
    }

    public boolean existsById(Long id) {
        return trasladoRepository.existsById(id);
    }
}