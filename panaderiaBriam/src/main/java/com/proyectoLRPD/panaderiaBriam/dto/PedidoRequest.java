package com.proyectoLRPD.panaderiaBriam.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PedidoRequest {
    private Long clienteId;
    private Integer cantidadBolsas;
    private String fechaPedido; // Formato YYYY-MM-DD
    private String horaEntrega; // Formato HH:mm:ss
    private BigDecimal precioActual; // El precio que configuraste en el celular
}