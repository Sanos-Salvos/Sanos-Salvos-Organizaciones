package com.sanosysalvos.organizaciones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
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