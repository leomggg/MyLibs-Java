package com.example.MyLibs.services;

import com.example.MyLibs.entities.Libro;
import com.example.MyLibs.exceptions.ResourceNotFoundException;
import com.example.MyLibs.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepo;

    public List<Libro> listarTodos() {
        return libroRepo.findAll();
    }

    public Libro guardarLibro(Libro libro) {
        return libroRepo.save(libro);
    }

    public Libro buscarPorId(Long id) {
        return libroRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Libro con ID " + id + " no encontrado"));
    }

    public void eliminarLibro(Long id) {
        if (!libroRepo.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: ID no existe");
        }
        libroRepo.deleteById(id);
    }
}
