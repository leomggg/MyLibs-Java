package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    @Query("SELECT c FROM Comentario c WHERE LOWER(c.libro.titulo) = LOWER(:titulo) AND LOWER(c.libro.autor) = LOWER(:autor) ORDER BY c.fecha DESC")
    List<Comentario> findByLibroTituloYAutor(@Param("titulo") String titulo, @Param("autor") String autor);
}