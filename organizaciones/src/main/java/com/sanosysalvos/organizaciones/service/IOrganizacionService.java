package com.sanosysalvos.organizaciones.service;

import com.sanosysalvos.organizaciones.dto.OrganizacionDTO;
import java.util.List;

public interface IOrganizacionService {
    OrganizacionDTO guardar(OrganizacionDTO dto);
    List<OrganizacionDTO> obtenerTodas();
    OrganizacionDTO obtenerPorId(Long id);
    OrganizacionDTO actualizar(Long id, OrganizacionDTO dto);
    void eliminar(Long id);
}