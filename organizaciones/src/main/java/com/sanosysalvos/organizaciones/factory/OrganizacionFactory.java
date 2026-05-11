package com.sanosysalvos.organizaciones.factory;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.model.Organizacion;

public class OrganizacionFactory {

    public static Organizacion crearOrganizacion(OrganizacionDTO dto) {
        Organizacion org = new Organizacion();
        org.setNombre(dto.getNombre());
        org.setDireccion(dto.getDireccion());
        org.setTelefono(dto.getTelefono());
        org.setEmail(dto.getEmail());

        // Lógica de Factory para definir el tipo
        switch (dto.getTipo().toUpperCase()) {
            case "VETERINARIA":
                org.setTipo("Veterinaria");
                break;
            case "REFUGIO":
                org.setTipo("Refugio");
                break;
            case "MUNICIPALIDAD":
                org.setTipo("Municipalidad");
                break;
            default:
                throw new IllegalArgumentException("Tipo de organización no válido");
        }
        return org;
    }
}