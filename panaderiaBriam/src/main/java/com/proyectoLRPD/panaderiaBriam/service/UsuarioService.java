package com.proyectoLRPD.panaderiaBriam.service;
import com.proyectoLRPD.panaderiaBriam.entity.Usuario;
import com.proyectoLRPD.panaderiaBriam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired private UsuarioRepository usuarioRepository;

    public Optional<Usuario> buscarPorUsername(String u) { return usuarioRepository.findByUsername(u); }
    public List<Usuario> listarUsuariosOrdenados() { return usuarioRepository.findAllByOrderByUsernameAsc(); }
    public Usuario crearUsuario(Usuario u) { return usuarioRepository.save(u); }
    public Usuario editarUsuario(Long id, Usuario u) {
        Usuario base = usuarioRepository.findById(id).orElseThrow();
        base.setNombres(u.getNombres()); base.setApellidos(u.getApellidos());
        base.setRol(u.getRol()); base.setActivo(u.getActivo());
        if(u.getPassword() != null && !u.getPassword().isEmpty()) base.setPassword(u.getPassword());
        return usuarioRepository.save(base);
    }
}