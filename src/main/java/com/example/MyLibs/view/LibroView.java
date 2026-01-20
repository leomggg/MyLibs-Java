package com.example.MyLibs.view;

import com.example.MyLibs.config.SecurityConfig; // Importar tu config de seguridad
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
import com.vaadin.flow.spring.security.AuthenticationContext; // Para el logout correcto
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.stream.Collectors;

@Route("")
@PageTitle("Comunidad | MyLibs")
@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
public class LibroView extends VerticalLayout {

    private final LibroService libroService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final ComentarioService comentarioService;
    private final transient AuthenticationContext authContext;

    private FlexLayout catalogLayout = new FlexLayout();
    private Tab allTab = new Tab("Muro Global 🌏");
    private Tab readTab = new Tab("Mis Leídos ✅");
    private Tab pendingTab = new Tab("Mis Pendientes ⏳");
    private Tabs filters = new Tabs(allTab, readTab, pendingTab);

    public LibroView(LibroService ls, CategoriaService cs, UsuarioService us, ComentarioService cms, AuthenticationContext authContext) {
        this.libroService = ls; this.categoriaService = cs;
        this.usuarioService = us; this.comentarioService = cms;
        this.authContext = authContext;

        setSizeFull();
        getStyle().set("background-color", "#f1f5f9");

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Span userLabel = new Span("Conectado como: " + currentUsername);
        userLabel.getStyle().set("font-weight", "bold").set("color", "#475569");

        Button btnLogout = new Button("Cerrar Sesión", VaadinIcon.EXIT.create(), e -> authContext.logout());
        btnLogout.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout userInfo = new HorizontalLayout(userLabel, btnLogout);
        userInfo.setAlignItems(Alignment.CENTER);

        HorizontalLayout topBar = new HorizontalLayout(new H2("MyLibs Social"), filters, userInfo);
        topBar.setWidthFull(); topBar.setAlignItems(Alignment.CENTER); topBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        topBar.getStyle().set("background", "white").set("padding", "10px 20px");

        Button btnAdd = new Button(VaadinIcon.PLUS.create(), e -> abrirDialogoNuevoLibro());
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnAdd.getStyle().set("position", "fixed").set("bottom", "30px").set("right", "30px").set("border-radius", "50%").set("width", "70px").set("height", "70px").set("z-index", "100");

        filters.addSelectedChangeListener(e -> actualizarCatalogo());
        catalogLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        catalogLayout.getStyle().set("gap", "20px").set("padding", "20px");

        add(topBar, new Hr(), catalogLayout, btnAdd);
        actualizarCatalogo();
    }

    private void abrirDialogoNuevoLibro() {
        Dialog d = new Dialog();
        d.setHeaderTitle("Añadir Libro al Muro");

        TextField t = new TextField("Título");
        TextField a = new TextField("Autor");
        ComboBox<Categoria> c = new ComboBox<>("Género");
        c.setItems(categoriaService.listarTodas());
        c.setItemLabelGenerator(Categoria::getNombre);

        TextArea s = new TextArea("Sinopsis");
        Checkbox l = new Checkbox("¿Lo has leído ya?");

        Button save = new Button("Publicar", e -> {
            Libro nuevo = new Libro();
            nuevo.setTitulo(t.getValue());
            nuevo.setAutor(a.getValue());
            nuevo.setCategoria(c.getValue());
            nuevo.setSinopsis(s.getValue());
            nuevo.setLeido(l.getValue());

            String user = SecurityContextHolder.getContext().getAuthentication().getName();
            usuarioService.buscarPorNombre(user).ifPresent(nuevo::setUsuario);

            libroService.guardarLibro(nuevo);
            actualizarCatalogo();
            d.close();
            Notification.show("Libro publicado en el muro global");
        });

        d.add(new VerticalLayout(t, a, c, s, l));
        d.getFooter().add(new Button("Cancelar", x -> d.close()), save);
        d.open();
    }

    private void actualizarCatalogo() {
        catalogLayout.removeAll();
        String current = SecurityContextHolder.getContext().getAuthentication().getName();
        libroService.listarTodos().stream()
                .filter(l -> {
                    // El Muro Global muestra todo. Las otras pestañas filtran por el usuario actual.
                    if (filters.getSelectedTab().equals(readTab)) return l.isLeido() && l.getUsuario().getUsername().equals(current);
                    if (filters.getSelectedTab().equals(pendingTab)) return !l.isLeido() && l.getUsuario().getUsername().equals(current);
                    return true;
                })
                .forEach(l -> catalogLayout.add(crearCard(l)));
    }

    private Component crearCard(Libro libro) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("240px");
        card.getStyle().set("background", "white").set("border-radius", "15px").set("box-shadow", "0 4px 6px rgba(0,0,0,0.1)");

        String uploader = libro.getUsuario() != null ? libro.getUsuario().getUsername() : "Anónimo";
        Span badgeUser = new Span("@" + uploader);
        badgeUser.getStyle().set("font-size", "0.7rem").set("color", "#3b82f6").set("font-weight", "bold");

        Div cover = new Div(new Icon(VaadinIcon.BOOK));
        cover.setWidthFull(); cover.setHeight("140px");
        cover.getStyle().set("background", "#f1f5f9").set("display", "flex").set("align-items", "center").set("justify-content", "center").set("border-radius", "10px").set("cursor", "pointer");
        cover.addClickListener(e -> mostrarDetalles(libro));

        Button btnToggle = new Button(libro.isLeido() ? "Leído ✅" : "Pendiente ⏳");
        btnToggle.addThemeVariants(libro.isLeido() ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_CONTRAST);
        btnToggle.getStyle().set("font-size", "0.75rem");
        btnToggle.addClickListener(e -> {
            libro.setLeido(!libro.isLeido());
            libroService.guardarLibro(libro);
            actualizarCatalogo();
        });

        card.add(badgeUser, cover, new H4(libro.getTitulo()), btnToggle);
        card.setAlignItems(Alignment.CENTER);
        return card;
    }

    private void mostrarDetalles(Libro libro) {
        Dialog d = new Dialog();
        d.setWidth("500px");
        d.setHeaderTitle(libro.getTitulo() + " (Subido por @" + libro.getUsuario().getUsername() + ")");

        VerticalLayout layout = new VerticalLayout();
        layout.add(new Html("<b>Sinopsis:</b>"), new Paragraph(libro.getSinopsis()));
        layout.add(new Hr(), new Html("<b>Opiniones de la comunidad:</b>"));

        comentarioService.listarPorLibro(libro).forEach(c -> {
            Div comm = new Div(new Span("<b>@" + c.getUsuario().getUsername() + "</b>: "), new Span(c.getTexto()));
            comm.getStyle().set("background", "#f1f5f9").set("padding", "8px").set("border-radius", "8px").set("width", "100%");
            layout.add(comm);
        });

        TextArea input = new TextArea("Añade tu comentario");
        input.setWidthFull();
        Button btnComm = new Button("Comentar", e -> {
            String u = SecurityContextHolder.getContext().getAuthentication().getName();
            usuarioService.buscarPorNombre(u).ifPresent(user -> {
                comentarioService.guardar(new Comentario(input.getValue(), user, libro));
                d.close();
                Notification.show("Comentario guardado");
                actualizarCatalogo();
            });
        });

        layout.add(input, btnComm);
        d.add(layout);
        d.open();
    }
}