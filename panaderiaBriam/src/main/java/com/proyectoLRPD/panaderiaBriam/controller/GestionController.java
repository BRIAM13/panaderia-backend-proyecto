package com.proyectoLRPD.panaderiaBriam.controller;

import com.proyectoLRPD.panaderiaBriam.dto.PedidoRequest;
import com.proyectoLRPD.panaderiaBriam.entity.Cliente;
import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.repository.UsuarioRepository;
import com.proyectoLRPD.panaderiaBriam.service.ClienteService;
import com.proyectoLRPD.panaderiaBriam.service.PedidoService;
import com.proyectoLRPD.panaderiaBriam.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==========================================
    // 1. GESTIÓN DE CLIENTES
    // ==========================================

    @GetMapping("/clientes")
    public List<Cliente> listarClientes() {
        return clienteService.listarTodos();
    }

    @PostMapping("/clientes")
    public Cliente crearCliente(@RequestBody Cliente cliente) {
        return clienteService.guardarCliente(cliente);
    }

    @PutMapping("/clientes/{id}")
    public Cliente editarCliente(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        Cliente c = clienteService.buscarPorId(id);
        if (c != null) {
            c.setNombreNegocio(clienteActualizado.getNombreNegocio());
            c.setDireccion(clienteActualizado.getDireccion());
            c.setTelefono(clienteActualizado.getTelefono());
            return clienteService.guardarCliente(c);
        }
        return null;
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==========================================
    // 2. GESTIÓN DE PEDIDOS (VENTAS)
    // ==========================================

    @PostMapping("/pedidos")
    public Pedido registrarPedido(@RequestBody PedidoRequest request) {
        Pedido pedido = new Pedido();

        // Cliente
        Cliente cliente = new Cliente();
        cliente.setId(request.getClienteId());
        pedido.setCliente(cliente);

        // Datos básicos
        pedido.setCantidadBolsas(request.getCantidadBolsas());
        pedido.setFechaPedido(LocalDate.parse(request.getFechaPedido()));

        // Hora de Entrega (Opcional)
        if (request.getHoraEntrega() != null && !request.getHoraEntrega().isEmpty()) {
            String horaStr = request.getHoraEntrega().length() == 5 ? request.getHoraEntrega() + ":00" : request.getHoraEntrega();
            pedido.setHoraEntrega(LocalTime.parse(horaStr));
        }

        // Precio dinámico
        if (request.getPrecioActual() != null) {
            pedido.setPrecioUnitario(request.getPrecioActual());
        }

        pedido.setEntregado(false); // Por defecto

        return pedidoService.registrarPedido(pedido);
    }

    @PutMapping("/pedidos/{id}")
    public ResponseEntity<?> editarPedido(@PathVariable Long id, @RequestBody PedidoRequest request) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id);
            pedido.setCantidadBolsas(request.getCantidadBolsas());

            // Actualizar hora si viene
            if (request.getHoraEntrega() != null && !request.getHoraEntrega().isEmpty()) {
                String horaStr = request.getHoraEntrega().length() == 5 ? request.getHoraEntrega() + ":00" : request.getHoraEntrega();
                pedido.setHoraEntrega(LocalTime.parse(horaStr));
            }

            // Recalcular total
            BigDecimal nuevoTotal = pedido.getPrecioUnitario().multiply(new BigDecimal(pedido.getCantidadBolsas()));
            pedido.setMontoTotal(nuevoTotal);

            return ResponseEntity.ok(pedidoService.registrarPedido(pedido));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/pedidos/{id}")
    public ResponseEntity<?> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.ok(Map.of("mensaje", "Eliminado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error al eliminar"));
        }
    }

    @GetMapping("/pedidos")
    public List<Pedido> listarPorFecha(@RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return pedidoService.listarPorFecha(fecha);
    }

    // Nuevo: Traer todo lo pendiente de entrega (Ruta Activa)
    @GetMapping("/pedidos/pendientes")
    public List<Pedido> obtenerRutaActiva() {
        return pedidoService.obtenerPendientesDeEntrega();
    }

    // ==========================================
    // 3. ENTREGAS Y COBRANZAS
    // ==========================================

    @PutMapping("/pedidos/{id}/entregar")
    public ResponseEntity<?> marcarEntregado(@PathVariable Long id) {
        try {
            Pedido p = pedidoService.obtenerPorId(id);
            p.setEntregado(true);
            pedidoService.registrarPedido(p);
            return ResponseEntity.ok(p);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/pedidos/{id}/pagar")
    public ResponseEntity<?> pagarPedido(@PathVariable Long id, @RequestParam(required = false) String usuario) {
        try {
            Pedido p = pedidoService.obtenerPorId(id);
            p.setEstadoPago(Pedido.EstadoPago.PAGADO);
            p.setEntregado(true);
            p.setFechaPago(LocalDateTime.now());
            if(usuario != null) p.setUsuarioCobro(usuario); // Auditoría

            pedidoService.registrarPedido(p);
            return ResponseEntity.ok(p);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/deudas")
    public List<Pedido> verDeudas() {
        return pedidoService.obtenerDeudas();
    }

    // ==========================================
    // 4. GESTIÓN DE USUARIOS (ADMIN)
    // ==========================================

    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAllByOrderByUsernameAsc();
    }

    @PostMapping("/usuarios")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioEdit) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombres(usuarioEdit.getNombres());
            u.setApellidos(usuarioEdit.getApellidos());
            u.setRol(usuarioEdit.getRol());
            u.setActivo(usuarioEdit.getActivo());
            if(usuarioEdit.getPassword() != null && !usuarioEdit.getPassword().isEmpty()){
                u.setPassword(usuarioEdit.getPassword());
            }
            usuarioRepository.save(u);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }
}