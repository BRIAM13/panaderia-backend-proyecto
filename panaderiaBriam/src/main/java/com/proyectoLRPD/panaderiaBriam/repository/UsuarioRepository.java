package com.proyectoLRPD.panaderiaBriam.repository;

import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    // ESTE ES EL QUE FALTA PARA LA LISTA ORDENADA
    List<Usuario> findAllByOrderByUsernameAsc();
}