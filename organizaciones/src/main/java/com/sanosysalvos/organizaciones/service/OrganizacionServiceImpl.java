package com.sanosysalvos.organizaciones.service;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.factory.IOrganizacionFactory;
import com.sanosysalvos.organizaciones.model.Organizacion;
import com.sanosysalvos.organizaciones.producer.OrganizacionProducer;
import com.sanosysalvos.organizaciones.repository.OrganizacionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallback")
    public OrganizacionDTO guardar(OrganizacionDTO dto) {
        Organizacion nuevaOrg = organizacionFactory.toEntity(dto);
        Organizacion orgGuardada = repository.save(nuevaOrg);
        kafkaProducer.enviarEventoRegistro("ORGANIZACION_CREADA: " + orgGuardada.getNombre());
        return organizacionFactory.toDTO(orgGuardada);
    }

    @Override
    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackListar")
    public List<OrganizacionDTO> obtenerTodas() {
        return repository.findAll().stream()
                .map(organizacionFactory::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackOptional")
    public OrganizacionDTO obtenerPorId(Long id) {
        Organizacion organizacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con el ID: " + id));
        return organizacionFactory.toDTO(organizacion);
    }

    @Override 
    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackActualizar")
    public OrganizacionDTO actualizar(Long id, OrganizacionDTO dto) {
        Organizacion organizacionExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con el ID: " + id));

        organizacionExistente.setNombre(dto.getNombre());
        organizacionExistente.setDireccion(dto.getDireccion());
        organizacionExistente.setTelefono(dto.getTelefono());

        Organizacion orgActualizada = repository.save(organizacionExistente);
        kafkaProducer.enviarEventoRegistro("ORGANIZACION_ACTUALIZADA: " + orgActualizada.getNombre());

        return organizacionFactory.toDTO(orgActualizada);
    }

    @Override
    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackEliminar")
    public void eliminar(Long id) {
        Organizacion organizacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar. Organización no encontrada con ID: " + id));
        repository.deleteById(id);
        kafkaProducer.enviarEventoRegistro("ORGANIZACION_ELIMINADA: " + organizacion.getNombre());
    }

    // =========================================================================
    // MÉTODOS FALLBACK (Ya no darán error porque OrganizacionDTO tendrá setId)
    // =========================================================================

    public OrganizacionDTO metodoFallback(OrganizacionDTO dto, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al registrar debido a: " + t.getMessage());
        OrganizacionDTO dtoRespaldo = new OrganizacionDTO();
        dtoRespaldo.setId(-1L);
        dtoRespaldo.setNombre("Servicio no disponible");
        return dtoRespaldo;
    }

    public OrganizacionDTO metodoFallbackActualizar(Long id, OrganizacionDTO dto, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al actualizar debido a: " + t.getMessage());
        OrganizacionDTO dtoRespaldo = new OrganizacionDTO();
        dtoRespaldo.setId(id);
        dtoRespaldo.setNombre("Servicio no disponible");
        return dtoRespaldo;
    }

    public List<OrganizacionDTO> metodoFallbackListar(Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al listar debido a: " + t.getMessage());
        return List.of();
    }

    public OrganizacionDTO metodoFallbackOptional(Long id, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al buscar ID " + id + " debido a: " + t.getMessage());
        OrganizacionDTO dtoRespaldo = new OrganizacionDTO();
        dtoRespaldo.setId(-1L);
        dtoRespaldo.setNombre("No disponible");
        return dtoRespaldo;
    }

    public void metodoFallbackEliminar(Long id, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al eliminar ID " + id + " debido a: " + t.getMessage());
    }
}