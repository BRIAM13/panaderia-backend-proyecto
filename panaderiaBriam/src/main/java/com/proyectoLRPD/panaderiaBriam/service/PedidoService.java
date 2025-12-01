package com.proyectoLRPD.panaderiaBriam.service;

import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import com.proyectoLRPD.panaderiaBriam.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Registrar pedido (La entidad ya calcula sola los 3 soles)
    public Pedido registrarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    // Listar por fecha (Calendario)
    public List<Pedido> listarPorFecha(LocalDate fecha) {
        return pedidoRepository.findByFechaPedido(fecha);
    }

    // Registrar Pago
    public Pedido registrarPago(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstadoPago(Pedido.EstadoPago.PAGADO);
        pedido.setFechaPago(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    // Ver Dashboard de Deudas
    public List<Pedido> obtenerDeudas() {
        // CORRECCIÓN: Solo devolvemos lo que está PENDIENTE de pago Y ADEMÁS ya fue ENTREGADO (true)
        return pedidoRepository.findByEstadoPagoAndEntregadoTrue(Pedido.EstadoPago.PENDIENTE);
    }
    // Buscar uno solo
    public Pedido obtenerPorId(Long id) {
        return pedidoRepository.findById(id).orElseThrow(() -> new RuntimeException("Pedido no existe"));
    }

    // Eliminar
    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
    public List<Pedido> obtenerPendientesDeEntrega() {
        return pedidoRepository.findByEntregadoFalseOrderByFechaPedidoDesc();
    }
}