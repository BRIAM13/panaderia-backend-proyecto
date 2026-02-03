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
                    if (!u.getPassword().equals(creds.get("password"))) return ResponseEntity.status(401).body(Map.of("mensaje", "Clave incorrecta"));
                    if (!u.getActivo()) return ResponseEntity.status(403).body(Map.of("mensaje", "Usuario inactivo"));
                    return ResponseEntity.ok(Map.of("usuario", u.getUsername(), "nombres", u.getNombres(), "rol", u.getRol(), "id", u.getId()));
                }).orElse(ResponseEntity.status(401).body(Map.of("mensaje", "No existe")));
    }
}