package com.proyectoLRPD.panaderiaBriam;

import com.proyectoLRPD.panaderiaBriam.entity.Cliente;
import com.proyectoLRPD.panaderiaBriam.repository.ClienteRepository;
import com.proyectoLRPD.panaderiaBriam.service.ClienteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void testListarClientes() {
        // Simulamos que la BD tiene 2 clientes
        Mockito.when(clienteRepository.findAll()).thenReturn(Arrays.asList(new Cliente(), new Cliente()));

        List<Cliente> resultado = clienteService.listarTodos();

        Assertions.assertEquals(2, resultado.size());
    }

    @Test
    void testGuardarCliente() {
        Cliente nuevo = new Cliente();
        nuevo.setNombreNegocio("Bodega Test");

        Mockito.when(clienteRepository.save(any(Cliente.class))).thenReturn(nuevo);

        Cliente guardado = clienteService.guardarCliente(nuevo);

        Assertions.assertNotNull(guardado);
        Assertions.assertEquals("Bodega Test", guardado.getNombreNegocio());
    }

    @Test
    void testBuscarPorId() {
        Cliente c = new Cliente();
        c.setId(1L);
        Mockito.when(clienteRepository.findById(1L)).thenReturn(Optional.of(c));

        Cliente encontrado = clienteService.buscarPorId(1L);
        Assertions.assertNotNull(encontrado);
    }
}