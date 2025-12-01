package com.proyectoLRPD.panaderiaBriam.repository;

import com.proyectoLRPD.panaderiaBriam.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Para listar solo los activos en la ruta diaria
    List<Cliente> findByActivoTrue();
}