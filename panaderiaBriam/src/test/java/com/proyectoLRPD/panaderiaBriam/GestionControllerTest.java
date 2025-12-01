package com.proyectoLRPD.panaderiaBriam;

import com.proyectoLRPD.panaderiaBriam.controller.GestionController;
import com.proyectoLRPD.panaderiaBriam.dto.PedidoRequest;
import com.proyectoLRPD.panaderiaBriam.entity.Cliente;
import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.repository.UsuarioRepository;
import com.proyectoLRPD.panaderiaBriam.service.ClienteService;
import com.proyectoLRPD.panaderiaBriam.service.PedidoService;
import com.proyectoLRPD.panaderiaBriam.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class GestionControllerTest {

    @Mock private ClienteService clienteService;
    @Mock private PedidoService pedidoService;
    @Mock private UsuarioService usuarioService;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private GestionController gestionController;

    @Test
    void testListarClientes() {
        Mockito.when(clienteService.listarTodos()).thenReturn(new ArrayList<>());
        List<Cliente> resultado = gestionController.listarClientes();
        Assertions.assertNotNull(resultado);
    }

    @Test
    void testRegistrarPedido() {
        PedidoRequest request = new PedidoRequest();
        request.setClienteId(1L);
        request.setCantidadBolsas(10);
        request.setFechaPedido("2025-12-01");
        request.setHoraEntrega("08:00");
        request.setPrecioActual(new BigDecimal("3.00"));

        Pedido pedidoSimulado = new Pedido();
        pedidoSimulado.setMontoTotal(new BigDecimal("30.00"));

        Mockito.when(pedidoService.registrarPedido(any(Pedido.class))).thenReturn(pedidoSimulado);

        Pedido resultado = gestionController.registrarPedido(request);
        Assertions.assertNotNull(resultado);
    }

    @Test
    void testPagarPedido() {
        Pedido p = new Pedido();
        Mockito.when(pedidoService.obtenerPorId(1L)).thenReturn(p);

        ResponseEntity<?> res = gestionController.pagarPedido(1L, "Admin");
        Assertions.assertEquals(200, res.getStatusCodeValue());
    }

    @Test
    void testListarUsuarios() {
        Mockito.when(usuarioRepository.findAllByOrderByUsernameAsc()).thenReturn(new ArrayList<>());
        List<Usuario> users = gestionController.listarUsuarios();
        Assertions.assertNotNull(users);
    }
}