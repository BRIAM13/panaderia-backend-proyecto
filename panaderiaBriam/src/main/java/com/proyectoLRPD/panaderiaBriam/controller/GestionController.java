package com.proyectoLRPD.panaderiaBriam.controller;

import com.proyectoLRPD.panaderiaBriam.dto.PedidoRequest;
import com.proyectoLRPD.panaderiaBriam.entity.*;
import com.proyectoLRPD.panaderiaBriam.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GestionController {

    @Autowired private ClienteService clienteService;
    @Autowired private PedidoService pedidoService;
    @Autowired private UsuarioService usuarioService;

    // =========================================
    // GESTIÓN DE CLIENTES
    // =========================================
    @GetMapping("/clientes")
    public List<Cliente> listC() { return clienteService.listarTodos(); }

    @PostMapping("/clientes")
    public Cliente createC(@RequestBody Cliente c) { return clienteService.guardarCliente(c); }

    @PutMapping("/clientes/{id}")
    public Cliente editC(@PathVariable Long id, @RequestBody Cliente c) { return clienteService.editarCliente(id, c); }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> delC(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // GESTIÓN DE PEDIDOS
    // ==========================================

    // 1. REGISTRAR
    @PostMapping("/pedidos")
    public Pedido regP(@RequestBody PedidoRequest req) {
        Pedido p = new Pedido();
        Cliente c = new Cliente();
        c.setId(req.getClienteId());
        p.setCliente(c);
        p.setCantidadBolsas(req.getCantidadBolsas());
        p.setFechaPedido(LocalDate.parse(req.getFechaPedido()));

        if (req.getHoraEntrega() != null && !req.getHoraEntrega().isEmpty()) {
            p.setHoraEntrega(LocalTime.parse(req.getHoraEntrega()));
        }

        if (req.getMontoTotalManual() != null && req.getMontoTotalManual().doubleValue() > 0) {
            p.setMontoTotal(req.getMontoTotalManual());
            p.setPrecioUnitario(req.getMontoTotalManual().divide(new BigDecimal(req.getCantidadBolsas()), 2, java.math.RoundingMode.HALF_UP));
        } else {
            p.setPrecioUnitario(req.getPrecioActual());
        }

        p.setEntregado(false);
        return pedidoService.registrarPedido(p);
    }

    // 2. EDITAR PEDIDO (ESTE FALTABA)
    @PutMapping("/pedidos/{id}")
    public ResponseEntity<?> editP(@PathVariable Long id, @RequestBody PedidoRequest req) {
        try {
            Pedido p = pedidoService.obtenerPorId(id);
            p.setCantidadBolsas(req.getCantidadBolsas());

            // Si el pedido es especial (monto manual)
            if (req.getMontoTotalManual() != null && req.getMontoTotalManual().doubleValue() > 0) {
                p.setMontoTotal(req.getMontoTotalManual());
                p.setPrecioUnitario(req.getMontoTotalManual().divide(new BigDecimal(req.getCantidadBolsas()), 2, java.math.RoundingMode.HALF_UP));
            } else {
                // Si es normal, recalculamos con el precio unitario que ya tenía
                BigDecimal nuevoTotal = p.getPrecioUnitario().multiply(new BigDecimal(req.getCantidadBolsas()));
                p.setMontoTotal(nuevoTotal);
            }

            Pedido actualizado = pedidoService.registrarPedido(p);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. ELIMINAR PEDIDO (ESTE FALTABA)
    @DeleteMapping("/pedidos/{id}")
    public ResponseEntity<?> delP(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            // Devolvemos JSON para que Retrofit en Android no de error de conexión
            return ResponseEntity.ok(Map.of("mensaje", "Pedido eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "No se pudo eliminar el pedido"));
        }
    }

    @GetMapping("/pedidos/pendientes")
    public List<Pedido> ruta() { return pedidoService.obtenerPendientesDeEntrega(); }

    @GetMapping("/deudas")
    public List<Pedido> deudas() { return pedidoService.obtenerDeudas(); }

    @PutMapping("/pedidos/{id}/entregar")
    public ResponseEntity<?> ent(@PathVariable Long id) {
        Pedido p = pedidoService.obtenerPorId(id);
        p.setEntregado(true);
        return ResponseEntity.ok(pedidoService.registrarPedido(p));
    }

    @PutMapping("/pedidos/{id}/pagar")
    public ResponseEntity<?> pag(@PathVariable Long id, @RequestParam String usuario) {
        Pedido p = pedidoService.obtenerPorId(id);
        p.setEstadoPago(Pedido.EstadoPago.PAGADO);
        p.setEntregado(true);
        p.setFechaPago(LocalDateTime.now());
        p.setUsuarioCobro(usuario);
        return ResponseEntity.ok(pedidoService.registrarPedido(p));
    }

    // ==========================================
    // GESTIÓN DE USUARIOS
    // ==========================================
    @GetMapping("/usuarios")
    public List<Usuario> listU() { return usuarioService.listarUsuariosOrdenados(); }

    @PostMapping("/usuarios")
    public Usuario createU(@RequestBody Usuario u) { return usuarioService.crearUsuario(u); }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> editU(@PathVariable Long id, @RequestBody Usuario u) {
        return ResponseEntity.ok(usuarioService.editarUsuario(id, u));
    }
}