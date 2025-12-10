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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDate fechaPedido;

    @Column(name = "hora_registro")
    private LocalTime horaRegistro;

    @Column(name = "hora_entrega")
    private LocalTime horaEntrega;

    @Column(nullable = false)
    private Integer cantidadBolsas;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoPago estadoPago;

    private LocalDateTime fechaPago;
    private Boolean entregado = false;

    @Column(name = "usuario_cobro")
    private String usuarioCobro;

    // LÓGICA AUTOMÁTICA
    @PrePersist
    public void calcularTotales() {
        if (this.horaRegistro == null) {
            this.horaRegistro = LocalTime.now();
        }

        // Si no mandas hora de entrega, por defecto se queda nula (o ponemos now si prefieres)
        // if (this.horaEntrega == null) { this.horaEntrega = LocalTime.now(); }

        // SOLO CALCULAMOS SI EL TOTAL ES NULO (Para respetar precios manuales)
        if (this.montoTotal == null) {
            if (this.precioUnitario == null) {
                this.precioUnitario = new BigDecimal("3.00");
            }
            if (this.cantidadBolsas != null) {
                this.montoTotal = this.precioUnitario.multiply(new BigDecimal(this.cantidadBolsas));
            }
        }

        if (this.estadoPago == null) {
            this.estadoPago = EstadoPago.PENDIENTE;
        }
    }

    public enum EstadoPago {
        PENDIENTE,
        PAGADO
    }
}