package com.proyectoLRPD.panaderiaBriam.repository;

import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos de una fecha específica (Para la Ruta)
    List<Pedido> findByFechaPedido(LocalDate fecha);

    // === AQUÍ ESTÁ LA CORRECCIÓN ===
    // Borramos el @Query para que Spring Boot use la lógica del nombre:
    // "Busca por Estado de Pago Y que Entregado sea Verdadero"
    List<Pedido> findByEstadoPagoAndEntregadoTrue(Pedido.EstadoPago estadoPago);
    // ===============================
    // Trae todo lo que no se ha entregado, ordenado por fecha (los viejos primero o ultimo segun prefieras)
    List<Pedido> findByEntregadoFalseOrderByFechaPedidoDesc();
}