package com.example.MyLibs.view;

import com.example.MyLibs.entities.Libro;
import com.example.MyLibs.services.LibroService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route("")
@RolesAllowed({"ROL_USER", "ROL_ADMIN"})
public class LibroView extends VerticalLayout {

    private final LibroService libroService;
    private Grid<Libro> grid = new Grid<>(Libro.class);
    private BeanValidationBinder<Libro> binder = new BeanValidationBinder<>(Libro.class);

    private TextField titulo = new TextField("Título");
    private TextField autor = new TextField("Autor");
    private TextField isbn = new TextField("ISBN");
    private BigDecimalField precio = new BigDecimalField("Precio");
    private Button btnGuardar = new Button("Añadir Libro");

    public LibroView(LibroService libroService) {
        this.libroService = libroService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);

        H1 tituloPagina = new H1("Gestión de Libros");
        Button btnLogout = new Button("Cerrar Sesión", e -> {
            getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"));
        });
        btnLogout.getStyle().set("background-color", "#ff4d4d");
        btnLogout.getStyle().set("color", "white");

        HorizontalLayout encabezado = new HorizontalLayout(tituloPagina, btnLogout);
        encabezado.setWidthFull();
        encabezado.setAlignItems(Alignment.CENTER);
        encabezado.setJustifyContentMode(JustifyContentMode.BETWEEN);

        FormLayout formulario = new FormLayout(titulo, autor, isbn, precio, btnGuardar);
        binder.bindInstanceFields(this);

        btnGuardar.addClickListener(e -> guardarLibro());
        btnGuardar.getThemeNames().add("primary");

        grid.setColumns("titulo", "autor", "isbn", "precio");
        grid.setHeight("400px");

        add(encabezado, formulario, grid);
        actualizarLista();
    }

    private void guardarLibro() {
        Libro nuevo = new Libro();
        try {
            if (binder.writeBeanIfValid(nuevo)) {
                libroService.guardarLibro(nuevo);
                Notification.show("Libro guardado con éxito");
                actualizarLista();
                binder.setBean(new Libro());
            } else {
                Notification.show("Por favor, revisa los errores en el formulario");
            }
        } catch (Exception ex) {
            Notification.show("Error al guardar: " + ex.getMessage());
        }
    }

    private void actualizarLista() {
        grid.setItems(libroService.listarTodos());
    }
}