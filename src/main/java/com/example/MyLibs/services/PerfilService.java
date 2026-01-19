package com.example.MyLibs.services;

import com.example.MyLibs.entities.Perfil;
import com.example.MyLibs.exceptions.ResourceNotFoundException;
import com.example.MyLibs.repository.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepo;

    public Perfil obtenerPorId(Long id) {
        return perfilRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado con ID: " + id));
    }

    @Transactional
    public Perfil actualizarPerfil(Long id, Perfil perfilDetalles) {
        Perfil perfil = obtenerPorId(id);

        perfil.setNombre(perfilDetalles.getNombre());
        perfil.setApellidos(perfilDetalles.getApellidos());
        perfil.setBio(perfilDetalles.getBio());
        perfil.setTlf(perfilDetalles.getTlf());

        return perfilRepo.save(perfil);
    }
}