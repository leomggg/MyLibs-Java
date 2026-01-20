package com.example.MyLibs.services;

import com.example.MyLibs.entities.Libro;
import com.example.MyLibs.repository.LibroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    @Test
    public void testCalculoMediaComunitaria() {
        Libro l1 = new Libro(); l1.setTitulo("Java"); l1.setAutor("Oracle"); l1.setPuntuacion(2);
        Libro l2 = new Libro(); l2.setTitulo("Java"); l2.setAutor("Oracle"); l2.setPuntuacion(4);

        when(libroRepository.findAll()).thenReturn(Arrays.asList(l1, l2));

        int media = libroService.obtenerMediaComunidad("Java", "Oracle");

        assertEquals(3, media, "La media de estrellas no es correcta");
    }
}