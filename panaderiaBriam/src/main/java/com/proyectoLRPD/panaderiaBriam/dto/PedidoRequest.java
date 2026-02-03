package com.proyectoLRPD.panaderiaBriam.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PedidoRequest {
    private Long clienteId;
    private Integer cantidadBolsas;
    private String fechaPedido;
    private String horaEntrega;
    private BigDecimal precioActual;
    private BigDecimal montoTotalManual;
}