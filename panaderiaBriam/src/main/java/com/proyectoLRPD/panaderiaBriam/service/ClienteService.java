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
        return clienteRepository.findAll();
    }

    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElse(null);
    }
    public void eliminarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}