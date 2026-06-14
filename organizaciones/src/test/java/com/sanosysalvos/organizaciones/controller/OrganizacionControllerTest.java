package com.sanosysalvos.organizaciones.controller;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.service.IOrganizacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizacionControllerTest {

    @Mock
    private IOrganizacionService service;

    @InjectMocks
    private OrganizacionController controller;

    private OrganizacionDTO sampleDTO;

    @BeforeEach
    void setUp() {
        sampleDTO = new OrganizacionDTO(1L, "Refugio Patitas", "Refugio", "Calle 123", "987654321", "info@patitas.cl");
    }

    // ==================== POST /crear ====================

    @Test
    void registrarOrganizacion_deberiaRetornar201ConDTO() {
        when(service.guardar(any(OrganizacionDTO.class))).thenReturn(sampleDTO);

        ResponseEntity<OrganizacionDTO> response = controller.registrarOrganizacion(sampleDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Refugio Patitas", response.getBody().getNombre());
    }

    @Test
    void registrarOrganizacion_deberiaRetornar503SiFallback() {
        OrganizacionDTO fallbackDTO = new OrganizacionDTO(-1L, "Servicio no disponible", null, null, null, null);
        when(service.guardar(any(OrganizacionDTO.class))).thenReturn(fallbackDTO);

        ResponseEntity<OrganizacionDTO> response = controller.registrarOrganizacion(sampleDTO);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(-1L, response.getBody().getId());
    }

    // ==================== GET /listar ====================

    @Test
    void listarTodas_deberiaRetornar200ConLista() {
        OrganizacionDTO dto2 = new OrganizacionDTO(2L, "Vet Central", "Veterinaria", "Av. X", "111", "vet@mail.cl");
        when(service.obtenerTodas()).thenReturn(List.of(sampleDTO, dto2));

        ResponseEntity<List<OrganizacionDTO>> response = controller.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void listarTodas_deberiaRetornarListaVacia() {
        when(service.obtenerTodas()).thenReturn(List.of());

        ResponseEntity<List<OrganizacionDTO>> response = controller.listarTodas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    // ==================== GET /buscar/{id} ====================

    @Test
    void buscarPorId_deberiaRetornar200SiExiste() {
        when(service.obtenerPorId(1L)).thenReturn(sampleDTO);

        ResponseEntity<OrganizacionDTO> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Refugio Patitas", response.getBody().getNombre());
    }

    @Test
    void buscarPorId_deberiaRetornar404SiNoExiste() {
        when(service.obtenerPorId(99L)).thenThrow(new RuntimeException("Organización no encontrada"));

        ResponseEntity<OrganizacionDTO> response = controller.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void buscarPorId_deberiaRetornar503SiFallback() {
        OrganizacionDTO fallbackDTO = new OrganizacionDTO(-1L, "No disponible", null, null, null, null);
        when(service.obtenerPorId(1L)).thenReturn(fallbackDTO);

        ResponseEntity<OrganizacionDTO> response = controller.buscarPorId(1L);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    // ==================== PUT /actualizar/{id} ====================

    @Test
    void actualizarOrganizacion_deberiaRetornar200SiExiste() {
        OrganizacionDTO updatedDTO = new OrganizacionDTO(1L, "Nuevo Nombre", "Refugio", "Nueva Dir", "999", "new@mail.cl");
        when(service.actualizar(eq(1L), any(OrganizacionDTO.class))).thenReturn(updatedDTO);

        ResponseEntity<?> response = controller.actualizarOrganizacion(1L, updatedDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizacionDTO body = (OrganizacionDTO) response.getBody();
        assertEquals("Nuevo Nombre", body.getNombre());
    }

    @Test
    void actualizarOrganizacion_deberiaRetornar404SiNoExiste() {
        when(service.actualizar(eq(99L), any(OrganizacionDTO.class)))
                .thenThrow(new RuntimeException("Organización no encontrada"));

        ResponseEntity<?> response = controller.actualizarOrganizacion(99L, sampleDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void actualizarOrganizacion_deberiaRetornar200ConFallbackDTO() {
        // The actualizar fallback returns id=originalId (not -1),
        // so the controller returns 200 with the fallback DTO.
        OrganizacionDTO fallbackDTO = new OrganizacionDTO(1L, "Servicio no disponible", null, null, null, null);
        when(service.actualizar(eq(1L), any(OrganizacionDTO.class))).thenReturn(fallbackDTO);

        ResponseEntity<?> response = controller.actualizarOrganizacion(1L, sampleDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        OrganizacionDTO body = (OrganizacionDTO) response.getBody();
        assertEquals("Servicio no disponible", body.getNombre());
    }

    // ==================== DELETE /eliminar/{id} ====================

    @Test
    void eliminarOrganizacion_deberiaRetornar200SiExiste() {
        doNothing().when(service).eliminar(1L);

        ResponseEntity<?> response = controller.eliminarOrganizacion(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Solicitud de eliminación procesada", response.getBody());
    }

    @Test
    void eliminarOrganizacion_deberiaRetornar404SiNoExiste() {
        doThrow(new RuntimeException("Organización no encontrada")).when(service).eliminar(99L);

        ResponseEntity<?> response = controller.eliminarOrganizacion(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
