package com.example.MyLibs.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rol")
public class Rol {

    //! falta lo de aquí
    private Long id;

    private String nombre;



}
