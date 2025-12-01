package com.proyectoLRPD.panaderiaBriam.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime; // Importamos LocalTime

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
    private LocalDate fechaPedido; // Fecha para el calendario (YYYY-MM-DD)

    @Column(name = "hora_registro")
    private LocalTime horaRegistro; // HORA automática de sistema

    // === AQUÍ ESTÁ LA VARIABLE QUE FALTABA ===
    @Column(name = "hora_entrega")
    private LocalTime horaEntrega; // HORA elegida por el usuario

    // ... otros campos ...

    @Column(name = "entregado")
    private Boolean entregado = false; // Por defecto no entregado

// ... getters y setters ...
    // =========================================

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

    @Column(name = "usuario_cobro")
    private String usuarioCobro; // El nombre del usuario que dio click a "Cobrar"

    // LÓGICA AUTOMÁTICA
    @PrePersist
    public void calcularTotales() {
        // 1. Hora de REGISTRO (Auditoría): Esta SÍ es automática siempre
        if (this.horaRegistro == null) {
            this.horaRegistro = LocalTime.now();
        }

        // 2. Hora de ENTREGA: LA DEJAMOS QUIETA.
        // Si es null, se queda null. Ya no forzamos LocalTime.now().

        // 3. Precios y Totales
        if (this.precioUnitario == null) {
            this.precioUnitario = new BigDecimal("3.00");
        }
        if (this.cantidadBolsas != null) {
            this.montoTotal = this.precioUnitario.multiply(new BigDecimal(this.cantidadBolsas));
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