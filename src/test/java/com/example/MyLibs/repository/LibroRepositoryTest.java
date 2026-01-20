package com.example.MyLibs.repository;

import com.example.MyLibs.entities.Libro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class LibroRepositoryTest {

    @Autowired
    private LibroRepository libroRepository;

    @Test
    public void testPersistenciaLibro() {
        Libro libro = new Libro();
        libro.setTitulo("Don Quijote");
        libro.setAutor("Cervantes");

        Libro guardado = libroRepository.save(libro);
        Optional<Libro> buscado = libroRepository.findById(guardado.getId());

        assertTrue(buscado.isPresent());
        assertEquals("Don Quijote", buscado.get().getTitulo());
    }
}