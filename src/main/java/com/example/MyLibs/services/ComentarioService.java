package com.example.MyLibs.services;

import com.example.MyLibs.entities.Comentario;
import com.example.MyLibs.entities.Libro;
import com.example.MyLibs.repository.ComentarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComentarioService {
    private final ComentarioRepository repository;

    public ComentarioService(ComentarioRepository repository) {
        this.repository = repository;
    }

    public void guardar(Comentario c) { repository.save(c); }
    public List<Comentario> listarPorLibro(Libro l) { return repository.findByLibroOrderByFechaDesc(l); }
}