package com.proyectoLRPD.panaderiaBriam.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PedidoRequest {
    private Long clienteId;
    private Integer cantidadBolsas; // O cantidad de panes si es especial
    private String fechaPedido;
    private String horaEntrega;
    private BigDecimal precioActual; // Precio unitario (solo para estándar)

    // NUEVO: Total manual para pedidos especiales
    private BigDecimal montoTotalManual;
}