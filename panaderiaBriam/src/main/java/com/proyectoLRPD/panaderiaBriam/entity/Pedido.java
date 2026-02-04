package com.proyectoLRPD.panaderiaBriam.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "pedidos")
@Data
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private LocalDate fechaPedido;
    private LocalTime horaRegistro;
    private LocalTime horaEntrega;
    private Integer cantidadBolsas;
    private BigDecimal precioUnitario;
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    private EstadoPago estadoPago;
    public enum EstadoPago { PENDIENTE, PAGADO }

    private LocalDateTime fechaPago;
    private Boolean entregado = false;
    private String usuarioCobro;

    @PrePersist
    public void prePersist() {
        if (this.horaRegistro == null) this.horaRegistro = LocalTime.now();
        if (this.estadoPago == null) this.estadoPago = EstadoPago.PENDIENTE;
        if (this.entregado == null) this.entregado = false;

        // SOLO calculamos si el montoTotal es nulo (es decir, es un pedido estándar)
        if (this.montoTotal == null && this.cantidadBolsas != null) {
            BigDecimal precio = (this.precioUnitario != null) ? this.precioUnitario : new BigDecimal("3.00");
            this.montoTotal = precio.multiply(new BigDecimal(this.cantidadBolsas));
            this.precioUnitario = precio;
        }
    }
}