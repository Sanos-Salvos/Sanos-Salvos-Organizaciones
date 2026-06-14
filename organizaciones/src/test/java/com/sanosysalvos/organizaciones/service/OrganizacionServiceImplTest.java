package com.sanosysalvos.organizaciones.service;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.factory.IOrganizacionFactory;
import com.sanosysalvos.organizaciones.model.Organizacion;
import com.sanosysalvos.organizaciones.producer.OrganizacionProducer;
import com.sanosysalvos.organizaciones.repository.OrganizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizacionServiceImplTest {

    @Mock
    private OrganizacionRepository repository;

    @Mock
    private OrganizacionProducer kafkaProducer;

    @Mock
    private IOrganizacionFactory factory;

    @InjectMocks
    private OrganizacionServiceImpl service;

    private OrganizacionDTO sampleDTO;
    private Organizacion sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDTO = new OrganizacionDTO(1L, "Refugio Patitas", "Refugio", "Calle 123", "987654321", "info@patitas.cl");
        sampleEntity = new Organizacion(1L, "Refugio Patitas", "Refugio", "Calle 123", "987654321", "info@patitas.cl");
    }

    // ==================== guardar ====================

    @Test
    void guardar_deberiaGuardarYRetornarDTO() {
        when(factory.toEntity(any(OrganizacionDTO.class))).thenReturn(sampleEntity);
        when(repository.save(any(Organizacion.class))).thenReturn(sampleEntity);
        when(factory.toDTO(any(Organizacion.class))).thenReturn(sampleDTO);

        OrganizacionDTO result = service.guardar(sampleDTO);

        assertNotNull(result);
        assertEquals("Refugio Patitas", result.getNombre());
        assertEquals(1L, result.getId());
        verify(repository).save(sampleEntity);
        verify(kafkaProducer).enviarEventoRegistro("ORGANIZACION_CREADA: Refugio Patitas");
    }

    @Test
    void guardar_deberiaLlamarFactoryToEntityYToDTO() {
        when(factory.toEntity(any(OrganizacionDTO.class))).thenReturn(sampleEntity);
        when(repository.save(any(Organizacion.class))).thenReturn(sampleEntity);
        when(factory.toDTO(any(Organizacion.class))).thenReturn(sampleDTO);

        service.guardar(sampleDTO);

        verify(factory).toEntity(sampleDTO);
        verify(factory).toDTO(sampleEntity);
    }

    // ==================== obtenerTodas ====================

    @Test
    void obtenerTodas_deberiaRetornarListaDeDTOs() {
        Organizacion entity2 = new Organizacion(2L, "Vet Central", "Veterinaria", "Av. Principal", "111", "vet@mail.cl");
        OrganizacionDTO dto2 = new OrganizacionDTO(2L, "Vet Central", "Veterinaria", "Av. Principal", "111", "vet@mail.cl");

        when(repository.findAll()).thenReturn(List.of(sampleEntity, entity2));
        when(factory.toDTO(sampleEntity)).thenReturn(sampleDTO);
        when(factory.toDTO(entity2)).thenReturn(dto2);

        List<OrganizacionDTO> result = service.obtenerTodas();

        assertEquals(2, result.size());
        assertEquals("Refugio Patitas", result.get(0).getNombre());
        assertEquals("Vet Central", result.get(1).getNombre());
    }

    @Test
    void obtenerTodas_deberiaRetornarListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        List<OrganizacionDTO> result = service.obtenerTodas();

        assertTrue(result.isEmpty());
    }

    // ==================== obtenerPorId ====================

    @Test
    void obtenerPorId_deberiaRetornarOrganizacion() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(factory.toDTO(sampleEntity)).thenReturn(sampleDTO);

        OrganizacionDTO result = service.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals("Refugio Patitas", result.getNombre());
    }

    @Test
    void obtenerPorId_deberiaLanzarExcepcionSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.obtenerPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    // ==================== actualizar ====================

    @Test
    void actualizar_deberiaActualizarNombreDireccionTelefono() {
        OrganizacionDTO updateDTO = new OrganizacionDTO();
        updateDTO.setNombre("Nuevo Nombre");
        updateDTO.setDireccion("Nueva Direccion");
        updateDTO.setTelefono("999");

        Organizacion updatedEntity = new Organizacion(1L, "Nuevo Nombre", "Refugio", "Nueva Direccion", "999", "info@patitas.cl");
        OrganizacionDTO updatedDTO = new OrganizacionDTO(1L, "Nuevo Nombre", "Refugio", "Nueva Direccion", "999", "info@patitas.cl");

        when(repository.findById(1L)).thenReturn(Optional.of(sampleEntity));
        when(repository.save(any(Organizacion.class))).thenReturn(updatedEntity);
        when(factory.toDTO(updatedEntity)).thenReturn(updatedDTO);

        OrganizacionDTO result = service.actualizar(1L, updateDTO);

        assertEquals("Nuevo Nombre", result.getNombre());
        assertEquals("Nueva Direccion", result.getDireccion());
        assertEquals("999", result.getTelefono());
        verify(kafkaProducer).enviarEventoRegistro("ORGANIZACION_ACTUALIZADA: Nuevo Nombre");
    }

    @Test
    void actualizar_deberiaLanzarExcepcionSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.actualizar(99L, sampleDTO));
    }

    // ==================== eliminar ====================

    @Test
    void eliminar_deberiaEliminarSiExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleEntity));

        service.eliminar(1L);

        verify(repository).deleteById(1L);
        verify(kafkaProducer).enviarEventoRegistro("ORGANIZACION_ELIMINADA: Refugio Patitas");
    }

    @Test
    void eliminar_deberiaLanzarExcepcionSiNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.eliminar(99L));
        verify(repository, never()).deleteById(anyLong());
    }
}
