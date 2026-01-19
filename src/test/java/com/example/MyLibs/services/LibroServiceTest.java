package com.example.MyLibs.services;

import com.example.MyLibs.entities.Libro;
import com.example.MyLibs.repository.LibroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LibroServiceTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroService libroService;

    @Test
    void testBuscarPorIdExitoso() {
        Libro libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Java para Expertos");

        when(libroRepository.findById(1L)).thenReturn(Optional.of(libro));

        Libro resultado = libroService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Java para Expertos", resultado.getTitulo());
    }
}