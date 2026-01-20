package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Comentario;
import com.example.MyLibs.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByLibroOrderByFechaDesc(Libro libro);
}