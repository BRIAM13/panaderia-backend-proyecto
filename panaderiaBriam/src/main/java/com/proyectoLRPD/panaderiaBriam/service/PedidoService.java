package com.proyectoLRPD.panaderiaBriam.service;
import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import com.proyectoLRPD.panaderiaBriam.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {
    @Autowired private PedidoRepository pedidoRepository;

    public Pedido registrarPedido(Pedido p) { return pedidoRepository.save(p); }
    public List<Pedido> listarPorFecha(LocalDate f) { return pedidoRepository.findByFechaPedido(f); }
    public List<Pedido> obtenerPendientesDeEntrega() { return pedidoRepository.findByEntregadoFalseOrderByFechaPedidoDesc(); }
    public List<Pedido> obtenerDeudas() { return pedidoRepository.findByEstadoPagoAndEntregadoTrue(Pedido.EstadoPago.PENDIENTE); }
    public Pedido obtenerPorId(Long id) { return pedidoRepository.findById(id).orElseThrow(); }
    public void eliminarPedido(Long id) { pedidoRepository.deleteById(id); }
}