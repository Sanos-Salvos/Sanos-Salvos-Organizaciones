package com.sanosysalvos.organizaciones.model;

import jakarta.persistence.*;
        import lombok.Data;

@Entity
@Table(name = "organizaciones")
@Data
public class Organizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String tipo;
    private String direccion;
    private String telefono;
    private String email;
}