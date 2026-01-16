package com.example.MyLibs.services;

import com.example.MyLibs.entities.Usuario;
import com.example.MyLibs.repository.RolRepository;
import com.example.MyLibs.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return null;
    }
}
