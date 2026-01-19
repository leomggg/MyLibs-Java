package com.example.MyLibs.services;

import com.example.MyLibs.entities.Rol;
import com.example.MyLibs.entities.Roles;
import com.example.MyLibs.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RolService {

    @Autowired
    private RolRepository rolRepo;

    public Optional<Rol> buscarPorNombre(Roles nombreRol) {
        return rolRepo.findByNombre(nombreRol);
    }

    public void guardar(Rol rol) {
        rolRepo.save(rol);
    }
}