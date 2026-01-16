package com.example.MyLibs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@Table(name = "perfiles")
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;
    private String bio;

    @NotNull
    private Long tlf;

    public Perfil(Long id, String nombre, String apellidos, String bio, Long tlf) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.bio = bio;
        this.tlf = tlf;
    }

    //! Relación 1:1 con Usuario
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @OneToOne(cascade = CascadeType.ALL)
    private Usuario usuario;
}
