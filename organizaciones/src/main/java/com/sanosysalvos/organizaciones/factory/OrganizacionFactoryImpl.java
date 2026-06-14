package com.sanosysalvos.organizaciones.factory;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.model.Organizacion;
import org.springframework.stereotype.Component;

@Component
public class OrganizacionFactoryImpl implements IOrganizacionFactory {

    @Override
    public Organizacion toEntity(OrganizacionDTO dto) {
        if (dto == null) {
            return null;
        }

        Organizacion org = new Organizacion();
        org.setId(dto.getId());
        org.setNombre(dto.getNombre());
        org.setDireccion(dto.getDireccion());
        org.setTelefono(dto.getTelefono());
        org.setEmail(dto.getEmail());

        if (dto.getTipo() != null) {
            String tipoFormateado = switch (dto.getTipo().toUpperCase()) {
                case "VETERINARIA" -> "Veterinaria";
                case "REFUGIO" -> "Refugio";
                case "MUNICIPALIDAD" -> "Municipalidad";
                default -> throw new IllegalArgumentException("Tipo de organización no válido: " + dto.getTipo());
            };
            org.setTipo(tipoFormateado);
        } else {
            throw new IllegalArgumentException("El tipo de organización no puede ser nulo");
        }

        return org;
    }

    @Override
    public OrganizacionDTO toDTO(Organizacion entity) {
        if (entity == null) {
            return null;
        }

        OrganizacionDTO dto = new OrganizacionDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setTipo(entity.getTipo());
        dto.setDireccion(entity.getDireccion());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());

        return dto;
    }
}