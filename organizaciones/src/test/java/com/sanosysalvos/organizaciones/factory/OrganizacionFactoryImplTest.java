package com.sanosysalvos.organizaciones.factory;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.model.Organizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrganizacionFactoryImplTest {

    @InjectMocks
    private OrganizacionFactoryImpl factory;

    private OrganizacionDTO sampleDTO;
    private Organizacion sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDTO = new OrganizacionDTO(1L, "Refugio Patitas", "REFUGIO", "Calle 123", "987654321", "info@patitas.cl");
        sampleEntity = new Organizacion(1L, "Refugio Patitas", "Refugio", "Calle 123", "987654321", "info@patitas.cl");
    }

    // ==================== toEntity ====================

    @Test
    void toEntity_deberiaMapearDTOAEntidad() {
        Organizacion result = factory.toEntity(sampleDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Refugio Patitas", result.getNombre());
        assertEquals("Refugio", result.getTipo());
        assertEquals("Calle 123", result.getDireccion());
        assertEquals("987654321", result.getTelefono());
        assertEquals("info@patitas.cl", result.getEmail());
    }

    @Test
    void toEntity_deberiaNormalizarTipoVeterinaria() {
        sampleDTO.setTipo("VETERINARIA");
        Organizacion result = factory.toEntity(sampleDTO);
        assertEquals("Veterinaria", result.getTipo());
    }

    @Test
    void toEntity_deberiaNormalizarTipoMunicipalidad() {
        sampleDTO.setTipo("MUNICIPALIDAD");
        Organizacion result = factory.toEntity(sampleDTO);
        assertEquals("Municipalidad", result.getTipo());
    }

    @Test
    void toEntity_deberiaLanzarExcepcionSiTipoInvalido() {
        sampleDTO.setTipo("INVALIDO");

        assertThrows(IllegalArgumentException.class, () -> factory.toEntity(sampleDTO));
    }

    @Test
    void toEntity_deberiaLanzarExcepcionSiTipoNulo() {
        sampleDTO.setTipo(null);

        assertThrows(IllegalArgumentException.class, () -> factory.toEntity(sampleDTO));
    }

    @Test
    void toEntity_deberiaRetornarNullSiDTONulo() {
        assertNull(factory.toEntity(null));
    }

    // ==================== toDTO ====================

    @Test
    void toDTO_deberiaMapearEntidadADTO() {
        OrganizacionDTO result = factory.toDTO(sampleEntity);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Refugio Patitas", result.getNombre());
        assertEquals("Refugio", result.getTipo());
        assertEquals("Calle 123", result.getDireccion());
        assertEquals("987654321", result.getTelefono());
        assertEquals("info@patitas.cl", result.getEmail());
    }

    @Test
    void toDTO_deberiaRetornarNullSiEntidadNula() {
        assertNull(factory.toDTO(null));
    }

    @Test
    void toDTO_deberiaMapearTodosLosCampos() {
        Organizacion entity = new Organizacion(5L, "Vet Central", "Veterinaria", "Av. Siempre Viva", "12345", "vet@mail.cl");
        OrganizacionDTO result = factory.toDTO(entity);

        assertEquals(5L, result.getId());
        assertEquals("Vet Central", result.getNombre());
        assertEquals("Veterinaria", result.getTipo());
        assertEquals("Av. Siempre Viva", result.getDireccion());
        assertEquals("12345", result.getTelefono());
        assertEquals("vet@mail.cl", result.getEmail());
    }

    // ==================== Round trip ====================

    @Test
    void toEntity_toDTO_deberiaPreservarDatos() {
        Organizacion entity = factory.toEntity(sampleDTO);
        OrganizacionDTO roundTrip = factory.toDTO(entity);

        assertEquals(sampleDTO.getId(), roundTrip.getId());
        assertEquals(sampleDTO.getNombre(), roundTrip.getNombre());
        assertEquals("Refugio", roundTrip.getTipo()); // normalized
        assertEquals(sampleDTO.getDireccion(), roundTrip.getDireccion());
        assertEquals(sampleDTO.getTelefono(), roundTrip.getTelefono());
        assertEquals(sampleDTO.getEmail(), roundTrip.getEmail());
    }
}
