package com.proyectoLRPD.panaderiaBriam.controller;
import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    @Autowired private UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        return usuarioService.buscarPorUsername(creds.get("username"))
                .map(u -> {
                    // 1. Verificar contraseña
                    if (!u.getPassword().equals(creds.get("password"))) {
                        return ResponseEntity.status(401).body(Map.of("mensaje", "Clave incorrecta"));
                    }
                    // 2. Verificar si está activo
                    if (u.getActivo() != null && !u.getActivo()) {
                        return ResponseEntity.status(403).body(Map.of("mensaje", "Usuario inactivo"));
                    }

                    // 3. Crear respuesta manual para evitar error de NULOS
                    java.util.Map<String, Object> respuesta = new java.util.HashMap<>();
                    respuesta.put("usuario", u.getUsername());
                    respuesta.put("nombres", u.getNombres() != null ? u.getNombres() : "");
                    respuesta.put("rol", u.getRol());
                    respuesta.put("id", u.getId());
                    respuesta.put("mensaje", "Login exitoso");

                    return ResponseEntity.ok(respuesta);
                }).orElse(ResponseEntity.status(401).body(Map.of("mensaje", "Usuario no existe")));
    }
}