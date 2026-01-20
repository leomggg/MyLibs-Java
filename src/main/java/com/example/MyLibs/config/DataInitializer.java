package com.example.MyLibs.config;

import com.example.MyLibs.entities.*;
import com.example.MyLibs.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UsuarioRepository usuarioRepo;
    private final RolRepository rolRepo;
    private final CategoriaRepository categoriaRepo;
    private final LibroRepository libroRepo;
    private final PasswordEncoder encoder;

    public DataInitializer(UsuarioRepository ur, RolRepository rr, CategoriaRepository cr, LibroRepository lr, PasswordEncoder pe) {
        this.usuarioRepo = ur; this.rolRepo = rr; this.categoriaRepo = cr; this.libroRepo = lr; this.encoder = pe;
    }

    @Override
    public void run(String... args) {
        if (rolRepo.count() == 0) {
            Rol adminRol = new Rol(); adminRol.setNombre(Roles.ROLE_ADMIN); rolRepo.save(adminRol);
            Rol userRol = new Rol(); userRol.setNombre(Roles.ROLE_USER); rolRepo.save(userRol);

            Map<String, Categoria> cats = new HashMap<>();
            List.of("Ficción", "Programación", "Fantasía", "Ciencia", "Historia", "Desarrollo Personal").forEach(n -> {
                Categoria c = new Categoria(); c.setNombre(n);
                cats.put(n, categoriaRepo.save(c));
            });

            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("1234"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRol));
            usuarioRepo.save(admin);

            List<Object[]> librosData = List.of(
                    new Object[]{"1984", "George Orwell", "Ficción", "9780451524935", "Obra maestra distópica."},
                    new Object[]{"Clean Code", "Robert C. Martin", "Programación", "9780132350884", "Manual de buenas prácticas."},
                    new Object[]{"El Hobbit", "J.R.R. Tolkien", "Fantasía", "9780261102217", "El viaje de Bilbo Bolsón."},
                    new Object[]{"Sapiens", "Yuval Noah Harari", "Ciencia", "9780062316097", "Breve historia de la humanidad."},
                    new Object[]{"El Principito", "Antoine de Saint-Exupéry", "Ficción", "9780156012195", "Un clásico para todas las edades."},
                    new Object[]{"Harry Potter y la Piedra Filosofal", "J.K. Rowling", "Fantasía", "9788478884452", "El inicio de la magia."},
                    new Object[]{"Don Quijote de la Mancha", "Miguel de Cervantes", "Ficción", "9788424115456", "El caballero de la triste figura."},
                    new Object[]{"Crónica de una muerte anunciada", "Gabriel García Márquez", "Ficción", "9781400034956", "Realismo mágico puro."},
                    new Object[]{"El Código Da Vinci", "Dan Brown", "Ficción", "9780307474278", "Misterio en el Louvre."},
                    new Object[]{"Rayuela", "Julio Cortázar", "Ficción", "9788437604572", "Novela para armar."},
                    new Object[]{"The Pragmatic Programmer", "Andrew Hunt", "Programación", "9780135957059", "Crecimiento profesional."},
                    new Object[]{"Breve historia del tiempo", "Stephen Hawking", "Ciencia", "9780553380163", "El cosmos explicado."},
                    new Object[]{"Los Pilares de la Tierra", "Ken Follett", "Historia", "9780451166890", "Construcción de una catedral."},
                    new Object[]{"El señor de los anillos", "J.R.R. Tolkien", "Fantasía", "9780618640157", "La comunidad del anillo."},
                    new Object[]{"Orgullo y Prejuicio", "Jane Austen", "Ficción", "9780141439518", "Drama romántico clásico."},
                    new Object[]{"Hábitos Atómicos", "James Clear", "Desarrollo Personal", "9780735211292", "Mejora un 1% cada día."},
                    new Object[]{"Cien años de soledad", "Gabriel García Márquez", "Ficción", "9780307474728", "La saga de los Buendía."},
                    new Object[]{"Refactoring", "Martin Fowler", "Programación", "9780134757599", "Mejorar el código existente."},
                    new Object[]{"El nombre de la rosa", "Umberto Eco", "Historia", "9788422614395", "Misterio medieval."},
                    new Object[]{"Crimen y Castigo", "Fiodor Dostoievski", "Ficción", "9780140449136", "Remordimiento y justicia."},
                    new Object[]{"Gente Normal", "Sally Rooney", "Ficción", "9781984822178", "Relaciones modernas."},
                    new Object[]{"El Psicoanalista", "John Katzenbach", "Ficción", "9788413143521", "Thriller psicológico."},
                    new Object[]{"Moby Dick", "Herman Melville", "Ficción", "9780142437247", "La ballena blanca."},
                    new Object[]{"El Silmarillion", "J.R.R. Tolkien", "Fantasía", "9780345325815", "Génesis de la Tierra Media."},
                    new Object[]{"Padre Rico Padre Pobre", "Robert Kiyosaki", "Desarrollo Personal", "9781612680194", "Educación financiera."},
                    new Object[]{"Design Patterns", "Erich Gamma", "Programación", "9780201633610", "Patrones de diseño."},
                    new Object[]{"Ensayo sobre la ceguera", "José Saramago", "Ficción", "9788420442600", "Distopía social."},
                    new Object[]{"La sombra del viento", "Carlos Ruiz Zafón", "Ficción", "9788408043645", "El cementerio de los libros olvidados."},
                    new Object[]{"Steve Jobs", "Walter Isaacson", "Historia", "9781451648539", "Biografía del genio de Apple."},
                    new Object[]{"Deep Work", "Cal Newport", "Desarrollo Personal", "9781455586691", "Concentración en un mundo ruidoso."}
            );

            for (Object[] d : librosData) {
                Libro l = new Libro();
                l.setTitulo((String) d[0]);
                l.setAutor((String) d[1]);
                l.setCategoria(cats.get((String) d[2]));
                l.setIsbn((String) d[3]);
                l.setSinopsis((String) d[4]);
                l.setUsuario(admin);
                l.setPrecio(BigDecimal.ZERO);
                l.setUrlPortada("https://covers.openlibrary.org/b/isbn/" + d[3] + "-L.jpg");
                libroRepo.save(l);
            }
            System.out.println(">>> 30 Libros con portadas reales cargados con éxito.");
        }
    }
}