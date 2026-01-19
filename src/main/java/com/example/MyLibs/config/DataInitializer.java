package com.example.MyLibs.config;

import com.example.MyLibs.entities.*;
import com.example.MyLibs.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final LibroRepository libroRepo;
    private final CategoriaRepository categoriaRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepo, RolRepository rolRepo,
                           LibroRepository libroRepo, CategoriaRepository categoriaRepo,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.libroRepo = libroRepo;
        this.categoriaRepo = categoriaRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Rol adminRol = new Rol();
        adminRol.setNombre(Roles.ROL_ADMIN);
        rolRepo.save(adminRol);

        Rol userRol = new Rol();
        userRol.setNombre(Roles.ROL_USER);
        rolRepo.save(userRol);

        Perfil adminPerfil = new Perfil();
        adminPerfil.setNombre("Admin");
        adminPerfil.setApellidos("Sistema");
        adminPerfil.setBio("Administrador principal de la biblioteca");
        adminPerfil.setTlf(600000000L);

        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setEnabled(true);
        admin.setRoles(Set.of(adminRol));

        admin.setPerfil(adminPerfil);
        adminPerfil.setUsuario(admin);

        usuarioRepo.save(admin);

        Categoria cat1 = new Categoria();
        cat1.setNombre("Programación");
        categoriaRepo.save(cat1);

        Libro l1 = new Libro();
        l1.setTitulo("Spring Boot Pro");
        l1.setAutor("Juan Perez");
        l1.setIsbn("9781234567890");
        l1.setPrecio(new BigDecimal("45.50"));
        l1.setCategoria(cat1);
        l1.setUsuario(admin);
        libroRepo.save(l1);

        System.out.println(">>> Base de datos inicializada: Usuario 'admin' creado correctamente.");
    }
}