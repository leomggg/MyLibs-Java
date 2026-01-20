package com.example.MyLibs.config;

import com.example.MyLibs.entities.*;
import com.example.MyLibs.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final CategoriaRepository categoriaRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(UsuarioRepository ur, RolRepository rr, CategoriaRepository cr, PasswordEncoder pe) {
        this.usuarioRepo = ur; this.rolRepo = rr; this.categoriaRepo = cr; this.encoder = pe;
    }

    @Override
    public void run(String... args) {
        if (rolRepo.count() == 0) {
            Rol adminRol = new Rol(); adminRol.setNombre(Roles.ROLE_ADMIN); rolRepo.save(adminRol);
            Rol userRol = new Rol(); userRol.setNombre(Roles.ROLE_USER); rolRepo.save(userRol);

            List.of("Ficción", "Programación", "Historia", "Fantasía", "Ciencia").forEach(nombre -> {
                Categoria cat = new Categoria();
                cat.setNombre(nombre);
                categoriaRepo.save(cat);
            });

            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("1234"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRol));
            usuarioRepo.save(admin);
            System.out.println(">>> Base de datos inicializada: admin/1234 y géneros creados.");
        }
    }
}