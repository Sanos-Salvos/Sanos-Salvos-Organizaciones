package com.sanosysalvos.organizaciones.factory;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import com.sanosysalvos.organizaciones.model.Organizacion;

public interface IOrganizacionFactory {
    Organizacion toEntity(OrganizacionDTO dto);
    OrganizacionDTO toDTO(Organizacion entity);
}