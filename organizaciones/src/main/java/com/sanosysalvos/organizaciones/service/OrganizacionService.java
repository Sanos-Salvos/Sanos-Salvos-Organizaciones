package com.sanosysalvos.organizaciones.service;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.factory.OrganizacionFactory;
import com.sanosysalvos.organizaciones.model.Organizacion;
import com.sanosysalvos.organizaciones.producer.OrganizacionProducer;
import com.sanosysalvos.organizaciones.repository.OrganizacionRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizacionService {

    @Autowired
    private OrganizacionRepository repository;

    @Autowired
    private OrganizacionProducer kafkaProducer;

    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallback")
    public Organizacion registrar(OrganizacionDTO dto) {
        Organizacion nuevaOrg = OrganizacionFactory.crearOrganizacion(dto);
        Organizacion orgGuardada = repository.save(nuevaOrg);

        kafkaProducer.enviarEventoRegistro("ORGANIZACION_CREADA: " + orgGuardada.getNombre());

        return orgGuardada;
    }

    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackListar")
    public List<Organizacion> obtenerTodas() {
        return repository.findAll();
    }

    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackOptional")
    public Optional<Organizacion> obtenerPorId(Long id) {
        return repository.findById(id);
    }

    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackActualizar")
    public Organizacion actualizar(Long id, OrganizacionDTO dto) {
        Organizacion organizacionExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con el ID: " + id));

        organizacionExistente.setNombre(dto.getNombre());
        organizacionExistente.setDireccion(dto.getDireccion());
        organizacionExistente.setTelefono(dto.getTelefono());

        Organizacion orgActualizada = repository.save(organizacionExistente);

        kafkaProducer.enviarEventoRegistro("ORGANIZACION_ACTUALIZADA: " + orgActualizada.getNombre());

        return orgActualizada;
    }

    @CircuitBreaker(name = "organizacionesCB", fallbackMethod = "metodoFallbackEliminar")
    public void eliminar(Long id) {
        Organizacion organizacion = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se puede eliminar. Organización no encontrada con ID: " + id));

        repository.deleteById(id);

        kafkaProducer.enviarEventoRegistro("ORGANIZACION_ELIMINADA: " + organizacion.getNombre());
    }

    public Organizacion metodoFallback(OrganizacionDTO dto, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al registrar debido a: " + t.getMessage());
        Organizacion orgRespaldo = new Organizacion();
        orgRespaldo.setId(-1L);
        orgRespaldo.setNombre("Servicio temporalmente no disponible (Registro fallido)");
        return orgRespaldo;
    }

    public Organizacion metodoFallbackActualizar(Long id, OrganizacionDTO dto, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al actualizar debido a: " + t.getMessage());
        Organizacion orgRespaldo = new Organizacion();
        orgRespaldo.setId(id);
        orgRespaldo.setNombre("Servicio temporalmente no disponible (Actualización fallida)");
        return orgRespaldo;
    }

    public List<Organizacion> metodoFallbackListar(Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al listar debido a: " + t.getMessage());

        return List.of();
    }

    public Optional<Organizacion> metodoFallbackOptional(Long id, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al buscar ID " + id + " debido a: " + t.getMessage());
        Organizacion orgRespaldo = new Organizacion();
        orgRespaldo.setId(-1L);
        orgRespaldo.setNombre("No disponible temporalmente");
        return Optional.of(orgRespaldo);
    }

    public void metodoFallbackEliminar(Long id, Throwable t) {
        System.err.println("El Circuit Breaker se ha activado al intentar eliminar ID " + id + " debido a: " + t.getMessage());
    }
}