package com.proyectoLRPD.panaderiaBriam;

import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.repository.UsuarioRepository;
import com.proyectoLRPD.panaderiaBriam.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testCrearUsuario() {
        Usuario u = new Usuario();
        u.setUsername("12345678");

        Mockito.when(usuarioRepository.save(any(Usuario.class))).thenReturn(u);

        Usuario creado = usuarioService.crearUsuario(u);
        Assertions.assertEquals("12345678", creado.getUsername());
    }

    @Test
    void testBuscarPorUsername() {
        Usuario u = new Usuario();
        u.setUsername("admin");
        Mockito.when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(u));

        Optional<Usuario> resultado = usuarioService.buscarPorUsername("admin");
        Assertions.assertTrue(resultado.isPresent());
    }
}