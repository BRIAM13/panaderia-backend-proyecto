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
@CrossOrigin(origins = "*") // Permite conexión desde cualquier IP (Celular/Emulador)
public class GestionController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    // ... (dentro de GestionController) ...

    @Autowired
    private UsuarioRepository usuarioRepository; // Inyectar el repositorio

    // LISTAR USUARIOS
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        // Usamos el nuevo método ordenado
        return usuarioRepository.findAllByOrderByUsernameAsc();
    }

    // EDITAR USUARIO (Para bloquear o cambiar datos)
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioEdit) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombres(usuarioEdit.getNombres());
            u.setApellidos(usuarioEdit.getApellidos());
            u.setRol(usuarioEdit.getRol());
            u.setActivo(usuarioEdit.getActivo());
            // Solo cambiamos contraseña si viene algo nuevo
            if(usuarioEdit.getPassword() != null && !usuarioEdit.getPassword().isEmpty()){
                u.setPassword(usuarioEdit.getPassword());
            }
            usuarioRepository.save(u);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ==========================================
    // GESTIÓN DE CLIENTES
    // ==========================================

    @GetMapping("/clientes")
    public List<Cliente> listarClientes() {
        return clienteService.listarTodos();
    }

    @PostMapping("/clientes")
    public Cliente crearCliente(@RequestBody Cliente cliente) {
        return clienteService.guardarCliente(cliente);
    }

    // ==========================================
    // GESTIÓN DE PEDIDOS (Ruta y Ventas)
    // ==========================================

    // 1. REGISTRAR NUEVO PEDIDO
    @PostMapping("/pedidos")
    public Pedido registrarPedido(@RequestBody PedidoRequest request) {
        Pedido pedido = new Pedido();

        // Asignamos el Cliente
        Cliente cliente = new Cliente();
        cliente.setId(request.getClienteId());
        pedido.setCliente(cliente);

        // Datos básicos
        pedido.setCantidadBolsas(request.getCantidadBolsas());
        pedido.setFechaPedido(LocalDate.parse(request.getFechaPedido()));

        // Hora de Entrega (Opcional)
        if (request.getHoraEntrega() != null && !request.getHoraEntrega().isEmpty()) {
            // Aseguramos formato HH:mm o HH:mm:ss
            String horaStr = request.getHoraEntrega().length() == 5 ? request.getHoraEntrega() + ":00" : request.getHoraEntrega();
            pedido.setHoraEntrega(LocalTime.parse(horaStr));
        }

        // Precio Configurado desde el Celular
        if (request.getPrecioActual() != null) {
            pedido.setPrecioUnitario(request.getPrecioActual());
        }

        // Por defecto no entregado
        pedido.setEntregado(false);

        return pedidoService.registrarPedido(pedido);
    }

    // 2. EDITAR PEDIDO (Modificar cantidad u hora)
    @PutMapping("/pedidos/{id}")
    public ResponseEntity<?> editarPedido(@PathVariable Long id, @RequestBody PedidoRequest request) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id);

            // Actualizamos cantidad
            pedido.setCantidadBolsas(request.getCantidadBolsas());

            // Actualizamos hora (si viene)
            if (request.getHoraEntrega() != null && !request.getHoraEntrega().isEmpty()) {
                String horaStr = request.getHoraEntrega().length() == 5 ? request.getHoraEntrega() + ":00" : request.getHoraEntrega();
                pedido.setHoraEntrega(LocalTime.parse(horaStr));
            }

            // Recalculamos el total automáticamente (Cantidad * Precio que ya tenía)
            BigDecimal nuevoTotal = pedido.getPrecioUnitario().multiply(new BigDecimal(pedido.getCantidadBolsas()));
            pedido.setMontoTotal(nuevoTotal);

            Pedido actualizado = pedidoService.registrarPedido(pedido);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. ELIMINAR PEDIDO
    @DeleteMapping("/pedidos/{id}")
    public ResponseEntity<?> eliminarPedido(@PathVariable Long id) {
        try {
            pedidoService.eliminarPedido(id);
            return ResponseEntity.ok(Map.of("mensaje", "Pedido eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "No se pudo eliminar"));
        }
    }

    // 4. LISTAR PEDIDOS POR FECHA (Para la Ruta del Día)
    @GetMapping("/pedidos")
    public List<Pedido> listarPorFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return pedidoService.listarPorFecha(fecha);
    }

    // ==========================================
    // CONTROL DE ENTREGAS Y COBRANZAS
    // ==========================================

    // 5. MARCAR COMO ENTREGADO (Pero NO pagado aún -> Fiado)
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

    // 6. PAGAR PEDIDO (Con auditoría)
    @PutMapping("/pedidos/{id}/pagar")
    public ResponseEntity<?> pagarPedido(
            @PathVariable Long id,
            @RequestParam String usuario) { // <--- NUEVO PARAMETRO
        try {
            Pedido p = pedidoService.obtenerPorId(id);
            p.setEstadoPago(Pedido.EstadoPago.PAGADO);
            p.setEntregado(true);
            p.setFechaPago(LocalDateTime.now());
            p.setUsuarioCobro(usuario); // <--- GUARDAMOS QUIÉN COBRÓ
            pedidoService.registrarPedido(p);
            return ResponseEntity.ok(p);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 7. VER DEUDAS (Pedidos pendientes de pago)
    @GetMapping("/deudas")
    public List<Pedido> verDeudas() {
        return pedidoService.obtenerDeudas();
    }

    // ==========================================
    // GESTIÓN DE USUARIOS (ADMIN)
    // ==========================================

    @PostMapping("/usuarios")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        // Encriptar contraseña aquí si fuera producción
        return usuarioService.crearUsuario(usuario);
    }
    // EDITAR CLIENTE
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

    // ELIMINAR CLIENTE
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    // 8. OBTENER RUTA ACTIVA (Todo lo no entregado)
    @GetMapping("/pedidos/pendientes")
    public List<Pedido> obtenerRutaActiva() {
        return pedidoService.obtenerPendientesDeEntrega();
    }
}