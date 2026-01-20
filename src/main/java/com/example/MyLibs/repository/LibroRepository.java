package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Libro;
import com.example.MyLibs.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTituloIgnoreCaseAndAutorIgnoreCaseAndUsuario(String titulo, String autor, Usuario usuario);
}