package com.proyectoLRPD.panaderiaBriam.service;

import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. LOGIN (Buscar por username)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Usuario login(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            if (u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    // 2. CREAR USUARIO
    public Usuario crearUsuario(Usuario usuario) {
        // Aquí podrías validar si ya existe
        return usuarioRepository.save(usuario);
    }

    // === AQUÍ ESTÁN LOS MÉTODOS QUE TE FALTABAN ===

    // 3. LISTAR USUARIOS ORDENADOS (Soluciona el primer error rojo)
    public List<Usuario> listarUsuariosOrdenados() {
        return usuarioRepository.findAllByOrderByUsernameAsc();
    }

    // 4. EDITAR USUARIO (Soluciona el segundo error rojo)
    public ResponseEntity<?> editarUsuario(Long id, Usuario usuarioEdit) {
        return usuarioRepository.findById(id).map(u -> {
            u.setNombres(usuarioEdit.getNombres());
            u.setApellidos(usuarioEdit.getApellidos());
            u.setRol(usuarioEdit.getRol());
            u.setActivo(usuarioEdit.getActivo());

            // Solo cambiamos contraseña si no viene vacía
            if (usuarioEdit.getPassword() != null && !usuarioEdit.getPassword().isEmpty()) {
                u.setPassword(usuarioEdit.getPassword());
            }

            usuarioRepository.save(u);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }
}