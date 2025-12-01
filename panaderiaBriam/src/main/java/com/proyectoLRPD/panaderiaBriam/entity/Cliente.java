package com.proyectoLRPD.panaderiaBriam.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombreNegocio; // Ej: "Broster's Briam"

    @Column(length = 100)
    private String nombreDueno;

    @Column(length = 200)
    private String direccion;

    @Column(length = 15)
    private String telefono;

    private Boolean activo = true;
}