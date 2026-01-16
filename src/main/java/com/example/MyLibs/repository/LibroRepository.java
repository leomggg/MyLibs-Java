package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByAutorContaning(String autor);
    List<Libro> findByCategoriaNombre(String nombreCategoria);
}
