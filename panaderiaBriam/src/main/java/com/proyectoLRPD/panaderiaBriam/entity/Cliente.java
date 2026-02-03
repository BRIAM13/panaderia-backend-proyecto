package com.proyectoLRPD.panaderiaBriam.entity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "clientes")
@Data
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreNegocio;
    private String nombreDueno;
    private String direccion;
    private String telefono;
    private Boolean activo = true;
}