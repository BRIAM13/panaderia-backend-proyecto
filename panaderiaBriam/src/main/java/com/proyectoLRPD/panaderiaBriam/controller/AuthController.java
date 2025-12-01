package com.proyectoLRPD.panaderiaBriam.controller;

import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        // 1. Buscamos al usuario por su DNI
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorUsername(username);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Usuario no encontrado"));
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Verificamos la contraseña
        if (!usuario.getPassword().equals(password)) {
            return ResponseEntity.status(401).body(Map.of("mensaje", "Contraseña incorrecta"));
        }

        // 3. ¡AQUÍ ESTÁ LA SEGURIDAD! Verificamos si está ACTIVO
        if (!usuario.getActivo()) {
            // Si activo es false, le prohibimos la entrada
            return ResponseEntity.status(403).body(Map.of("mensaje", "⚠️ Tu cuenta ha sido deshabilitada. Contacta al Admin."));
        }

        // 4. Si pasó todo, bienvenido
        return ResponseEntity.ok(Map.of(
                "mensaje", "Login Exitoso",
                "usuario", usuario.getUsername(),
                "nombres", (usuario.getNombres() != null ? usuario.getNombres() : ""), // Enviamos nombres para saludo
                "rol", usuario.getRol(),
                "id", usuario.getId()
        ));
    }
}