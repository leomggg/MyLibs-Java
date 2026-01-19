package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "perfiles")
public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    //De momento nada...
}
