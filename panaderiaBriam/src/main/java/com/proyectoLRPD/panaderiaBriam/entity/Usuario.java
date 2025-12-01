package com.proyectoLRPD.panaderiaBriam.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // DNI

    @Column(nullable = false)
    private String password;

    private String nombres;
    private String apellidos;
    private String rol; // ADMIN, REPARTIDOR, ETC.

    private Boolean activo = true; // Para banear/deshabilitar acceso
}