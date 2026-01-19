package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Rol;
import com.example.MyLibs.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "roles")
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(Roles rol);
}
