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

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginExitoso() {
        // Datos de prueba
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("username", "12345678");
        credenciales.put("password", "123456");

        Usuario usuarioSimulado = new Usuario();
        usuarioSimulado.setUsername("12345678");
        usuarioSimulado.setPassword("123456");
        usuarioSimulado.setActivo(true);
        usuarioSimulado.setNombres("Juan");
        usuarioSimulado.setRol("ADMIN");

        // Simulamos que el servicio encuentra al usuario
        Mockito.when(usuarioService.buscarPorUsername("12345678"))
                .thenReturn(Optional.of(usuarioSimulado));

        // Ejecutamos el login
        ResponseEntity<?> respuesta = authController.login(credenciales);

        // Verificamos que sea 200 OK
        Assertions.assertEquals(200, respuesta.getStatusCodeValue());
    }

    @Test
    void testLoginFallidoPassword() {
        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("username", "12345678");
        credenciales.put("password", "MAL");

        Usuario usuarioSimulado = new Usuario();
        usuarioSimulado.setUsername("12345678");
        usuarioSimulado.setPassword("BIEN");

        Mockito.when(usuarioService.buscarPorUsername("12345678"))
                .thenReturn(Optional.of(usuarioSimulado));

        ResponseEntity<?> respuesta = authController.login(credenciales);

        // Verificamos que sea 401 Unauthorized
        Assertions.assertEquals(401, respuesta.getStatusCodeValue());
    }
}