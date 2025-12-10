package com.proyectoLRPD.panaderiaBriam.repository;

import com.proyectoLRPD.panaderiaBriam.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Ordenar alfabéticamente por nombre del negocio
    List<Cliente> findAllByOrderByNombreNegocioAsc();
}