package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(path = "libros")
public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByAutorContaining(String autor);
    List<Libro> findByCategoriaNombre(String nombreCategoria);
}
