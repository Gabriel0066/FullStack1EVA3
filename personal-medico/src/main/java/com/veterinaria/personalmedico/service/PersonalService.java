package com.veterinaria.personalmedico.service;

import com.veterinaria.personalmedico.dto.PersonalDTO;
import com.veterinaria.personalmedico.model.Personal;
import com.veterinaria.personalmedico.repository.PersonalRepository;
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
public class PersonalService {

    private final PersonalRepository personalRepository;


    private PersonalDTO convertToDTO(Personal personal) {
        if (personal == null) return null;
        return new PersonalDTO(
                personal.getIdTrabajador(),
                personal.getRol(),
                personal.getNombre(),
                personal.getApellido(),
                personal.getRut(),
                personal.getCorreo(),
                personal.getTelefono(),
                personal.getDireccion()
        );
    }

    private Personal convertToEntity(PersonalDTO dto) {
        if (dto == null) return null;
        return Personal.builder()
                .idTrabajador(dto.getIdTrabajador())
                .rol(dto.getRol())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .rut(dto.getRut())
                .correo(dto.getCorreo())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .build();
    }

    public List<PersonalDTO> findAll() {
        log.info("Buscando todo el personal médico");
        return personalRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Optional<PersonalDTO> findById(Long id) {
        log.info("Buscando personal con ID: {}", id);
        return personalRepository.findById(id).map(this::convertToDTO);
    }

    public PersonalDTO save(PersonalDTO personalDTO) {
        log.info("Guardando personal médico");
        if (personalRepository.existsByRut(personalDTO.getRut())) {
            throw new IllegalArgumentException("Ya existe personal con el RUT: " + personalDTO.getRut());
        }
        if (personalRepository.existsByCorreo(personalDTO.getCorreo())) {
            throw new IllegalArgumentException("Ya existe personal con el correo: " + personalDTO.getCorreo());
        }
        Personal personal = convertToEntity(personalDTO);
        return convertToDTO(personalRepository.save(personal));
    }

    public PersonalDTO update(Long id, PersonalDTO personalDetailsDTO) {
        log.info("Actualizando personal médico con ID: {}", id);
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal no encontrado con ID: " + id));

        if (!personal.getRut().equals(personalDetailsDTO.getRut()) && personalRepository.existsByRut(personalDetailsDTO.getRut())) {
            throw new IllegalArgumentException("Ya existe personal con el RUT: " + personalDetailsDTO.getRut());
        }
        if (!personal.getCorreo().equals(personalDetailsDTO.getCorreo()) && personalRepository.existsByCorreo(personalDetailsDTO.getCorreo())) {
            throw new IllegalArgumentException("Ya existe personal con el correo: " + personalDetailsDTO.getCorreo());
        }

        personal.setRol(personalDetailsDTO.getRol());
        personal.setNombre(personalDetailsDTO.getNombre());
        personal.setApellido(personalDetailsDTO.getApellido());
        personal.setRut(personalDetailsDTO.getRut());
        personal.setCorreo(personalDetailsDTO.getCorreo());
        personal.setTelefono(personalDetailsDTO.getTelefono());
        personal.setDireccion(personalDetailsDTO.getDireccion());

        return convertToDTO(personalRepository.save(personal));
    }

    public void deleteById(Long id) {
        log.info("Eliminando personal con ID: {}", id);
        if (!personalRepository.existsById(id)) {
            throw new IllegalStateException("Personal no encontrado con ID: " + id);
        }
        personalRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return personalRepository.existsById(id);
    }

    public boolean existsByRut(String rut) {
        return personalRepository.existsByRut(rut);
    }

    public boolean existsByCorreo(String correo) {
        return personalRepository.existsByCorreo(correo);
    }
}