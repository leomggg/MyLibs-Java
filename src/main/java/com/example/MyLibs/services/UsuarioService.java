package com.example.MyLibs.services;

import com.example.MyLibs.entities.Rol;
import com.example.MyLibs.entities.Roles;
import com.example.MyLibs.entities.Usuario;
import com.example.MyLibs.repository.RolRepository;
import com.example.MyLibs.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private RolRepository rolRepo;

    //! Nota: Necesitarás BCryptPasswordEncoder cuando configures Security
    //! private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    public Usuario registrarUser(Usuario usuario) {
        if(usuarioRepo.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        Rol rolUser = rolRepo.findByNombre(Roles.ROL_USER).orElseThrow(() -> new RuntimeException("Error: Rol no encontrado"));
        usuario.getRoles().add(rolUser);

        // usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepo.save(usuario);
    }

    public Optional<Usuario> buscarPorNombre(String username) {
        return usuarioRepo.findByUsername(username);
    }

}
