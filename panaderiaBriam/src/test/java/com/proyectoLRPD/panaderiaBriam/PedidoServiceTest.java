package com.proyectoLRPD.panaderiaBriam;

import com.proyectoLRPD.panaderiaBriam.entity.Pedido;
import com.proyectoLRPD.panaderiaBriam.repository.PedidoRepository;
import com.proyectoLRPD.panaderiaBriam.service.PedidoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class) //Mockito para simular la BD
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void testCalculoDePrecioAutomatico() {
        // Simulamos un pedido de 3 bolsas
        Pedido pedidoNuevo = new Pedido();
        pedidoNuevo.setCantidadBolsas(3);

        // Cuando el repositorio guarde, le decimos que devuelva el mismo pedido
        Mockito.when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        pedidoNuevo.calcularTotales();
        Pedido resultado = pedidoService.registrarPedido(pedidoNuevo);

        // VERIFICAR
        Assertions.assertEquals(new BigDecimal("9.00"), resultado.getMontoTotal());
        Assertions.assertEquals(new BigDecimal("3.00"), resultado.getPrecioUnitario());
        Assertions.assertEquals(Pedido.EstadoPago.PENDIENTE, resultado.getEstadoPago());

        System.out.println("Resultado del Test : El cálculo de 3 bolsas x 3.00 fue: " + resultado.getMontoTotal());
    }
    @Test
    void testCalculoPrecioConCantidadAlta() {
        // Simulamos un pedido grande (100 bolsas)
        Pedido pedidoGrande = new Pedido();
        pedidoGrande.setCantidadBolsas(100);

        // Simulamos que el repositorio devuelve el mismo objeto
        Mockito.when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        // Forzamos el cálculo
        pedidoGrande.calcularTotales();
        Pedido resultado = pedidoService.registrarPedido(pedidoGrande);

        // Verificamos: 100 * 3.00 = 300.00
        Assertions.assertEquals(new java.math.BigDecimal("300.00"), resultado.getMontoTotal());
        System.out.println("✅ TEST GRANDE APROBADO: 100 bolsas = " + resultado.getMontoTotal());
    }
}
