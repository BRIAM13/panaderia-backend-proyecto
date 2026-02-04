package com.proyectoLRPD.panaderiaBriam.controller;

import com.proyectoLRPD.panaderiaBriam.dto.PedidoRequest;
import com.proyectoLRPD.panaderiaBriam.entity.*;
import com.proyectoLRPD.panaderiaBriam.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    // CLIENTES
    @GetMapping("/clientes")
    public List<Cliente> listC() {
        return clienteService.listarTodos();
    }

    @PostMapping("/clientes")
    public Cliente createC(@RequestBody Cliente c) {
        return clienteService.guardarCliente(c);
    }

    @PutMapping("/clientes/{id}")
    public Cliente editC(@PathVariable Long id, @RequestBody Cliente c) {
        return clienteService.editarCliente(id, c);
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> delC(@PathVariable Long id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.ok().build();
    }

    // PEDIDOS
    @PostMapping("/pedidos")
    public Pedido regP(@RequestBody PedidoRequest req) {
        Pedido p = new Pedido();
        Cliente c = new Cliente();
        c.setId(req.getClienteId());
        p.setCliente(c);

        p.setCantidadBolsas(req.getCantidadBolsas()); // Aquí viajan "panes" si es especial
        p.setFechaPedido(LocalDate.parse(req.getFechaPedido()));

        if (req.getHoraEntrega() != null && !req.getHoraEntrega().isEmpty()) {
            p.setHoraEntrega(LocalTime.parse(req.getHoraEntrega()));
        }

        // --- LÓGICA DE PEDIDO ESPECIAL CORREGIDA ---
        if (req.getMontoTotalManual() != null && req.getMontoTotalManual().doubleValue() > 0) {
            // Caso Especial: El usuario puso el precio a mano
            p.setMontoTotal(req.getMontoTotalManual());
            // Calculamos un precio unitario ficticio (Total / Cantidad) para no dejarlo en nulo
            p.setPrecioUnitario(req.getMontoTotalManual().divide(new java.math.BigDecimal(req.getCantidadBolsas()), 2, java.math.RoundingMode.HALF_UP));
        } else {
            // Caso Estándar: Usa el precio por bolsa configurado
            p.setPrecioUnitario(req.getPrecioActual());
            // El montoTotal se calculará en la entidad Pedido (@PrePersist)
        }

        p.setEntregado(false);
        return pedidoService.registrarPedido(p);
    }

    @GetMapping("/pedidos/pendientes")
    public List<Pedido> ruta() {
        return pedidoService.obtenerPendientesDeEntrega();
    }

    @GetMapping("/deudas")
    public List<Pedido> deudas() {
        return pedidoService.obtenerDeudas();
    }

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

    // USUARIOS
    @GetMapping("/usuarios")
    public List<Usuario> listU() {
        return usuarioService.listarUsuariosOrdenados();
    }

    @PostMapping("/usuarios")
    public Usuario createU(@RequestBody Usuario u) {
        return usuarioService.crearUsuario(u);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> editU(@PathVariable Long id, @RequestBody Usuario u) {
        return ResponseEntity.ok(usuarioService.editarUsuario(id, u));
    }
}