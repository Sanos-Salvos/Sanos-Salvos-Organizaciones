package com.sanosysalvos.organizaciones.service;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.factory.IOrganizacionFactory;
import com.sanosysalvos.organizaciones.model.Organizacion;
import com.sanosysalvos.organizaciones.producer.OrganizacionProducer;
import com.sanosysalvos.organizaciones.repository.OrganizacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizacionServiceImpl implements IOrganizacionService {

    private final OrganizacionRepository repository;
    private final OrganizacionProducer kafkaProducer;
    private final IOrganizacionFactory organizacionFactory;

    public OrganizacionServiceImpl(OrganizacionRepository repository,
                                   OrganizacionProducer kafkaProducer,
                                   IOrganizacionFactory organizacionFactory) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
        this.organizacionFactory = organizacionFactory;
    }

    @Override
    public OrganizacionDTO guardar(OrganizacionDTO dto) {
        Organizacion nuevaOrg = organizacionFactory.toEntity(dto);
        Organizacion orgGuardada = repository.save(nuevaOrg);

        kafkaProducer.enviarEventoRegistro("ORGANIZACION_CREADA: " + orgGuardada.getNombre());

        return organizacionFactory.toDTO(orgGuardada);
    }

    @Override
    public List<OrganizacionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(organizacionFactory::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrganizacionDTO obtenerPorId(Long id) {
        // Si no existe, lanza la excepción que tu GlobalExceptionHandler convertirá en un 404 real
        Organizacion organizacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con el ID: " + id));
        return organizacionFactory.toDTO(organizacion);
    }

    @Override
    public OrganizacionDTO actualizar(Long id, OrganizacionDTO dto) {
        Organizacion organizacionExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con el ID: " + id));

        organizacionExistente.setNombre(dto.getNombre());
        organizacionExistente.setDireccion(dto.getDireccion());
        organizacionExistente.setTelefono(dto.getTelefono());

        organizacionExistente.setEmail(dto.getEmail());

        Organizacion orgActualizada = repository.save(organizacionExistente);
        kafkaProducer.enviarEventoRegistro("ORGANIZACION_ACTUALIZADA: " + orgActualizada.getNombre());

        return organizacionFactory.toDTO(orgActualizada);
    }

    @Override
    public void eliminar(Long id) {
        Organizacion organizacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar. Organización no encontrada con ID: " + id));

        repository.deleteById(id);
        kafkaProducer.enviarEventoRegistro("ORGANIZACION_ELIMINADA: " + organizacion.getNombre());
    }
}