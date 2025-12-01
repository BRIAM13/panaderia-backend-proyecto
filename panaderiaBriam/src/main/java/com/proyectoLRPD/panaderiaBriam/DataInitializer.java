package com.proyectoLRPD.panaderiaBriam;

import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        // CAMBIA "12345678" POR TU DNI REAL
        String miDni = "72701801";

        // CAMBIA "mypassword" POR LA CONTRASEÑA QUE TÚ QUIERAS
        String miPassword = "AdminRonceros";

        // Verificamos si ya existes, si no, te crea
        if (usuarioRepository.findByUsername(miDni).isEmpty()) {
            Usuario miUsuario = new Usuario();
            miUsuario.setUsername(miDni); // Aquí guardamos el DNI
            miUsuario.setPassword(miPassword);
            miUsuario.setRol("ADMIN"); // Tú eres el jefe

            usuarioRepository.save(miUsuario);
            System.out.println("✅ USUARIO CREADO -> DNI: " + miDni + " / Pass: " + miPassword);
        }
    }
}