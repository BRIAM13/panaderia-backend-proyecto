package com.proyectoLRPD.panaderiaBriam.repository;
import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByFechaPedido(LocalDate fecha);
    List<Pedido> findByEntregadoFalseOrderByFechaPedidoDesc();
    List<Pedido> findByEstadoPagoAndEntregadoTrue(Pedido.EstadoPago estado);
}