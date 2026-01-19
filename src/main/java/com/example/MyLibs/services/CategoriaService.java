package com.example.MyLibs.services;

import com.example.MyLibs.entities.Categoria;
import com.example.MyLibs.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepo;

    public List<Categoria> listarTodas() {
        return categoriaRepo.findAll();
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepo.save(categoria);
    }
}