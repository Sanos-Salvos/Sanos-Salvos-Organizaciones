package com.sanosysalvos.organizaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizacionDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private String direccion;
    private String telefono;
    private String email;
}