package com.proyectoLRPD.panaderiaBriam;

import com.proyectoLRPD.panaderiaBriam.controller.AuthController;
import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginExitoso() {
        // 1. Datos que enviaremos
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("username", "70809010");
        credenciales.put("password", "123456");

        // 2. Usuario que existe en la "Base de Datos Falsa"
        Usuario usuarioSimulado = new Usuario();
        usuarioSimulado.setId(1L);
        usuarioSimulado.setUsername("70809010");
        usuarioSimulado.setPassword("123456");
        usuarioSimulado.setActivo(true); // Importante: Activo
        usuarioSimulado.setNombres("Test");
        usuarioSimulado.setRol("ADMIN");

        // 3. Configuramos el simulador: "Si te piden CUALQUIER usuario, devuelve este"
        Mockito.when(usuarioService.buscarPorUsername(anyString()))
                .thenReturn(Optional.of(usuarioSimulado));

        // 4. Ejecutamos el login
        ResponseEntity<?> respuesta = authController.login(credenciales);

        // 5. Verificamos que sea 200 OK
        Assertions.assertNotNull(respuesta);
        Assertions.assertEquals(200, respuesta.getStatusCodeValue());
        System.out.println("✅ TEST LOGIN PASÓ: Código " + respuesta.getStatusCodeValue());
    }

    @Test
    void testLoginFallidoPassword() {
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("username", "70809010");
        credenciales.put("password", "MAL_PASSWORD");

        Usuario usuarioSimulado = new Usuario();
        usuarioSimulado.setUsername("70809010");
        usuarioSimulado.setPassword("PASSWORD_REAL");
        usuarioSimulado.setActivo(true);

        Mockito.when(usuarioService.buscarPorUsername(anyString()))
                .thenReturn(Optional.of(usuarioSimulado));

        ResponseEntity<?> respuesta = authController.login(credenciales);

        // Debe dar 401 porque la contraseña no coincide
        Assertions.assertEquals(401, respuesta.getStatusCodeValue());
        System.out.println("✅ TEST LOGIN FALLIDO PASÓ: Código " + respuesta.getStatusCodeValue());
    }
}