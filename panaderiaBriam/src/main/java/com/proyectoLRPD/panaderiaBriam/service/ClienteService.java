package com.proyectoLRPD.panaderiaBriam.service;

import com.proyectoLRPD.panaderiaBriam.entity.Cliente;
import com.proyectoLRPD.panaderiaBriam.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAllByOrderByNombreNegocioAsc();
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    // ESTE ES EL MÉTODO QUE TE DABA ERROR
    public Cliente editarCliente(Long id, Cliente c) {
        Cliente base = buscarPorId(id);
        base.setNombreNegocio(c.getNombreNegocio());
        base.setDireccion(c.getDireccion());
        base.setTelefono(c.getTelefono());
        return clienteRepository.save(base);
    }

    // ESTE TAMBIÉN ES NECESARIO
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}