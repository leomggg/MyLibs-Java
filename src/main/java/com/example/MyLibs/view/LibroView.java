package com.example.MyLibs.view;

import com.example.MyLibs.entities.*;
import com.example.MyLibs.services.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
@PageTitle("Comunidad MyLibs")
@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
public class LibroView extends VerticalLayout {

    private final LibroService libroService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;

    private TextField titulo = new TextField("Título");
    private TextField autor = new TextField("Autor");
    private ComboBox<Categoria> categoria = new ComboBox<>("Género");
    private TextArea sinopsis = new TextArea("Sinopsis");
    private Checkbox leido = new Checkbox("¿Ya lo has leído?");

    private FlexLayout catalogLayout = new FlexLayout();
    private Tab allTab = new Tab("Muro Global 🌏");
    private Tab readTab = new Tab("Mis Leídos ✅");
    private Tab pendingTab = new Tab("Mis Pendientes ⏳");
    private Tabs filters = new Tabs(allTab, readTab, pendingTab);

    public LibroView(LibroService libroService, CategoriaService categoriaService, UsuarioService usuarioService) {
        this.libroService = libroService;
        this.categoriaService = categoriaService;
        this.usuarioService = usuarioService;

        setSizeFull();
        getStyle().set("background-color", "#f8fafc");

        H2 header = new H2("📖 MyLibs Social");
        Button btnLogout = new Button("Salir", VaadinIcon.SIGN_OUT.create(), e -> {
            getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"));
        });
        btnLogout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button btnFloatingAdd = new Button(VaadinIcon.PLUS.create(), e -> abrirDialogoNuevoLibro());
        btnFloatingAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnFloatingAdd.getStyle()
                .set("position", "fixed").set("bottom", "30px").set("right", "30px")
                .set("border-radius", "50%").set("width", "70px").set("height", "70px")
                .set("z-index", "100").set("box-shadow", "0 10px 20px rgba(0,0,0,0.3)");

        HorizontalLayout topBar = new HorizontalLayout(header, filters, btnLogout);
        topBar.setWidthFull();
        topBar.setAlignItems(Alignment.CENTER);
        topBar.setJustifyContentMode(JustifyContentMode.BETWEEN);

        filters.addSelectedChangeListener(e -> actualizarCatalogo());

        catalogLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        catalogLayout.getStyle().set("gap", "25px").set("padding", "20px");

        add(topBar, new Hr(), catalogLayout, btnFloatingAdd);
        actualizarCatalogo();
    }

    private void abrirDialogoNuevoLibro() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Añadir a la biblioteca");

        categoria.setItems(categoriaService.listarTodas());
        categoria.setItemLabelGenerator(Categoria::getNombre);
        sinopsis.setPlaceholder("Escribe un breve resumen...");

        FormLayout form = new FormLayout(titulo, autor, categoria, sinopsis, leido);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        Libro nuevoLibro = new Libro();
        BeanValidationBinder<Libro> binder = new BeanValidationBinder<>(Libro.class);
        binder.bindInstanceFields(this); // Ahora sí encontrará los campos declarados arriba
        binder.setBean(nuevoLibro);

        Button btnSave = new Button("Publicar", VaadinIcon.PAPERPLANE.create(), e -> {
            if (binder.validate().isOk()) {
                String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
                usuarioService.buscarPorNombre(currentUser).ifPresent(nuevoLibro::setUsuario);
                libroService.guardarLibro(nuevoLibro);
                actualizarCatalogo();
                dialog.close();
                Notification.show("¡Libro publicado!");
            }
        });
        btnSave.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(form);
        dialog.getFooter().add(new Button("Cancelar", x -> dialog.close()), btnSave);
        dialog.open();
    }

    private void actualizarCatalogo() {
        catalogLayout.removeAll();
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Libro> todos = libroService.listarTodos();

        if (filters.getSelectedTab().equals(readTab)) {
            todos = todos.stream().filter(l -> l.getUsuario() != null && l.getUsuario().getUsername().equals(currentUser) && l.isLeido()).collect(Collectors.toList());
        } else if (filters.getSelectedTab().equals(pendingTab)) {
            todos = todos.stream().filter(l -> l.getUsuario() != null && l.getUsuario().getUsername().equals(currentUser) && !l.isLeido()).collect(Collectors.toList());
        }

        todos.forEach(l -> catalogLayout.add(crearCard(l)));
    }

    private Component crearCard(Libro libro) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("230px");
        card.getStyle().set("background", "white").set("border-radius", "15px")
                .set("box-shadow", "0 8px 16px rgba(0,0,0,0.1)").set("padding", "15px");

        Div cover = new Div(new Icon(VaadinIcon.BOOK));
        cover.setWidthFull(); cover.setHeight("150px");
        cover.getStyle().set("background", "#f1f5f9").set("display", "flex")
                .set("align-items", "center").set("justify-content", "center")
                .set("border-radius", "10px").set("cursor", "pointer").set("color", "#94a3b8");
        cover.addClickListener(e -> mostrarDetalles(libro));

        H4 t = new H4(libro.getTitulo());
        t.getStyle().set("margin", "10px 0 0 0").set("cursor", "pointer");
        t.addClickListener(e -> mostrarDetalles(libro));

        Span userLabel = new Span("@" + (libro.getUsuario() != null ? libro.getUsuario().getUsername() : "sistema"));
        userLabel.getStyle().set("color", "#3b82f6").set("font-size", "0.75rem").set("font-weight", "bold");

        HorizontalLayout stars = new HorizontalLayout();
        for (int i = 1; i <= 5; i++) {
            final int val = i;
            Icon s = (i <= libro.getPuntuacion()) ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
            s.setSize("16px");
            s.getStyle().set("color", i <= libro.getPuntuacion() ? "#eab308" : "#cbd5e1").set("cursor", "pointer");
            s.addClickListener(e -> {
                libro.setPuntuacion(val);
                libroService.guardarLibro(libro);
                actualizarCatalogo();
                Notification.show("Valoración guardada");
            });
            stars.add(s);
        }

        card.add(cover, userLabel, t, new Span(libro.getAutor()), stars);
        card.setAlignItems(Alignment.CENTER);
        return card;
    }

    private void mostrarDetalles(Libro libro) {
        Dialog details = new Dialog();
        details.setWidth("600px");
        details.setHeaderTitle(libro.getTitulo());

        VerticalLayout layout = new VerticalLayout();

        Html sinopsisLabel = new Html("<div style='font-weight:bold; color:#64748b;'>Sinopsis:</div>");
        Paragraph sinopsisText = new Paragraph(libro.getSinopsis() != null ? libro.getSinopsis() : "Este libro aún no tiene sinopsis.");

        Html reviewLabel = new Html("<div style='font-weight:bold; color:#64748b;'>Opiniones de la comunidad:</div>");

        TextArea commentField = new TextArea();
        commentField.setWidthFull();
        commentField.setPlaceholder("Escribe tu opinión sobre este libro...");
        commentField.setValue(libro.getComentarios() != null ? libro.getComentarios() : "");

        String autorPost = libro.getUsuario() != null ? libro.getUsuario().getUsername() : "Anónimo";
        commentField.setLabel("Reseña escrita por: @" + autorPost);

        Checkbox checkLeido = new Checkbox("¡Me lo he leído! ✅", libro.isLeido());

        Button btnSaveDetails = new Button("Guardar cambios", e -> {
            libro.setComentarios(commentField.getValue());
            libro.setLeido(checkLeido.getValue());
            libroService.guardarLibro(libro);
            actualizarCatalogo();
            details.close();
            Notification.show("¡Diario actualizado!");
        });
        btnSaveDetails.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(sinopsisLabel, sinopsisText, new Hr(), reviewLabel, commentField, checkLeido, btnSaveDetails);
        details.add(layout);
        details.getFooter().add(new Button("Cerrar", e -> details.close()));
        details.open();
    }
}