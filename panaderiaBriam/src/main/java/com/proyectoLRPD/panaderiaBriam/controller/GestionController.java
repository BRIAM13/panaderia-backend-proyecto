package com.proyectoLRPD.panaderiaBriam.controller;

import com.proyectoLRPD.panaderiaBriam.dto.PedidoRequest;
import com.proyectoLRPD.panaderiaBriam.entity.Cliente;
import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.repository.ClienteRepository;
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
    private ClienteRepository clienteRepository; // Para el ordenamiento directo

    // ==========================================
    // GESTIÓN DE CLIENTES
    // ==========================================

    @GetMapping("/clientes")
    public List<Cliente> listarClientes() {
        // CAMBIO: Usamos el método que ordena alfabéticamente
        return clienteRepository.findAllByOrderByNombreNegocioAsc();
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
    // GESTIÓN DE PEDIDOS (VENTAS)
    // ==========================================

    @PostMapping("/pedidos")
    public Pedido registrarPedido(@RequestBody PedidoRequest request) {
        Pedido pedido = new Pedido();

        Cliente cliente = new Cliente();
        cliente.setId(request.getClienteId());
        pedido.setCliente(cliente);

        pedido.setCantidadBolsas(request.getCantidadBolsas());
        pedido.setFechaPedido(LocalDate.parse(request.getFechaPedido()));

        if (request.getHoraEntrega() != null && !request.getHoraEntrega().isEmpty()) {
            String horaStr = request.getHoraEntrega().length() == 5 ? request.getHoraEntrega() + ":00" : request.getHoraEntrega();
            pedido.setHoraEntrega(LocalTime.parse(horaStr));
        }

        // === LÓGICA DE PEDIDO ESPECIAL ===
        if (request.getMontoTotalManual() != null) {
            // Si viene monto manual, lo usamos directo y ponemos precio unitario en 0
            pedido.setMontoTotal(request.getMontoTotalManual());
            pedido.setPrecioUnitario(BigDecimal.ZERO);
        } else {
            // Si es normal, usamos el precio del celular
            if (request.getPrecioActual() != null) {
                pedido.setPrecioUnitario(request.getPrecioActual());
            }
            // El total se calculará solo en la entidad
        }
        // =================================

        pedido.setEntregado(false);
        return pedidoService.registrarPedido(pedido);
    }

    @PutMapping("/pedidos/{id}")
    public ResponseEntity<?> editarPedido(@PathVariable Long id, @RequestBody PedidoRequest request) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id);
            pedido.setCantidadBolsas(request.getCantidadBolsas());

            if (request.getHoraEntrega() != null && !request.getHoraEntrega().isEmpty()) {
                String horaStr = request.getHoraEntrega().length() == 5 ? request.getHoraEntrega() + ":00" : request.getHoraEntrega();
                pedido.setHoraEntrega(LocalTime.parse(horaStr));
            }

            // Recalculamos total (Lógica simple para editar)
            if(pedido.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal nuevoTotal = pedido.getPrecioUnitario().multiply(new BigDecimal(pedido.getCantidadBolsas()));
                pedido.setMontoTotal(nuevoTotal);
            }
            // Si era precio especial (0), no recalculamos, mantenemos el monto original o tendrías que enviar el nuevo monto manual

            Pedido actualizado = pedidoService.registrarPedido(pedido);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/pedidos/{id}")
    public ResponseEntity<?> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.ok(Map.of("mensaje", "Pedido eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "No se pudo eliminar"));
        }
    }

    @GetMapping("/pedidos")
    public List<Pedido> listarPorFecha(@RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return pedidoService.listarPorFecha(fecha);
    }

    @GetMapping("/pedidos/pendientes")
    public List<Pedido> obtenerRutaActiva() {
        return pedidoService.obtenerPendientesDeEntrega();
    }

    // ==========================================
    // COBRANZAS Y ENTREGAS
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
    public ResponseEntity<?> pagarPedido(@PathVariable Long id, @RequestParam String usuario) {
        try {
            Pedido p = pedidoService.obtenerPorId(id);
            p.setEstadoPago(Pedido.EstadoPago.PAGADO);
            p.setEntregado(true);
            p.setFechaPago(LocalDateTime.now());
            p.setUsuarioCobro(usuario);
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
    // USUARIOS
    // ==========================================

    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuariosOrdenados(); // Asegúrate de tener este método en Service o usa repository directo
    }

    @PostMapping("/usuarios")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioEdit) {
        return usuarioService.editarUsuario(id, usuarioEdit);
    }
}