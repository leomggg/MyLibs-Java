package com.example.MyLibs.services;

import com.example.MyLibs.entities.Libro;
import com.example.MyLibs.entities.Usuario;
import com.example.MyLibs.repository.LibroRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {
    private final LibroRepository repository;

    public LibroService(LibroRepository repository) {
        this.repository = repository;
    }

    public List<Libro> listarTodos() { return repository.findAll(); }
    public void guardarLibro(Libro l) { repository.save(l); }

    public Optional<Libro> buscarMiCopia(String titulo, String autor, Usuario usuario) {
        return repository.findByTituloIgnoreCaseAndAutorIgnoreCaseAndUsuario(titulo, autor, usuario);
    }

    public int obtenerMediaComunidad(String titulo, String autor) {
        List<Libro> copias = repository.findAll().stream()
                .filter(l -> l.getTitulo().equalsIgnoreCase(titulo) && l.getAutor().equalsIgnoreCase(autor))
                .filter(l -> l.getPuntuacion() > 0)
                .toList();

        if (copias.isEmpty()) return 0;

        double media = copias.stream()
                .mapToInt(Libro::getPuntuacion)
                .average()
                .orElse(0.0);

        return (int) Math.round(media);
    }
}