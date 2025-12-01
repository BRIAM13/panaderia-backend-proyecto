package com.proyectoLRPD.panaderiaBriam.repository;

import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    // Listar todos ordenados por Username (DNI) de menor a mayor
    List<Usuario> findAllByOrderByUsernameAsc();
}