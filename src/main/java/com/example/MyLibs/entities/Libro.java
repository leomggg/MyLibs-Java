package com.example.MyLibs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    private String autor;

    @Pattern(regexp = "^(978|979)\\d{10}$", message = "El ISBN debe ser un formato válido de 13 dígitos (empezando por 978 o 979)")
    private String isbn;

    @NotNull
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    public Libro(Long id, String titulo, String autor, String isbn, BigDecimal precio) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.precio = precio;
    }

    //! Relación 1:N con Categoría
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "categoria_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Categoria categoría;

}
