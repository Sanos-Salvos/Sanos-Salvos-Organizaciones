package com.sanosysalvos.organizaciones.dto;

import lombok.Data;

@Data
public class OrganizacionDTO {
    private String nombre;
    private String tipo;
    private String direccion;
    private String telefono;
    private String email;
}