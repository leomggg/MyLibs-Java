package com.example.MyLibs.view;

import com.example.MyLibs.entities.*;
import com.example.MyLibs.services.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.tabs.*;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
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

        String currentName = SecurityContextHolder.getContext().getAuthentication().getName();
        Button btnLogout = new Button("Cerrar Sesión", VaadinIcon.EXIT.create(), e -> authContext.logout());
        btnLogout.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout topBar = new HorizontalLayout(new H2("MyLibs Social"), filters, new HorizontalLayout(new Span("Usuario: " + currentName), btnLogout));
        topBar.setWidthFull(); topBar.setAlignItems(Alignment.CENTER); topBar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        topBar.getStyle().set("background", "white").set("padding", "10px 20px");

        Button btnAdd = new Button(VaadinIcon.PLUS.create(), e -> abrirDialogoNuevoLibro());
        btnAdd.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        btnAdd.getStyle().set("position", "fixed").set("bottom", "30px").set("right", "30px")
                .set("border-radius", "50%").set("width", "70px").set("height", "70px")
                .set("z-index", "100").set("box-shadow", "0 10px 20px rgba(0,0,0,0.3)");

        filters.addSelectedChangeListener(e -> actualizarCatalogo());
        catalogLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        catalogLayout.getStyle().set("gap", "20px").set("padding", "20px");

        add(topBar, new Hr(), catalogLayout, btnAdd);
        actualizarCatalogo();
    }

    private void actualizarCatalogo() {
        catalogLayout.removeAll();
        String currentName = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario currentUser = usuarioService.buscarPorNombre(currentName).orElse(null);
        List<Libro> todos = libroService.listarTodos();

        if (filters.getSelectedTab().equals(allTab)) {
            Collection<Libro> unicos = todos.stream().collect(Collectors.toMap(
                    l -> (l.getTitulo() + l.getAutor()).toLowerCase(), l -> l, (a, b) -> a)).values();
            unicos.forEach(l -> catalogLayout.add(crearCard(l, currentUser)));
        } else {
            todos.stream()
                    .filter(l -> l.getUsuario().getUsername().equals(currentName))
                    .filter(l -> filters.getSelectedTab().equals(readTab) ? l.isLeido() : !l.isLeido())
                    .forEach(l -> catalogLayout.add(crearCard(l, currentUser)));
        }
    }

    private Component crearCard(Libro libroBase, Usuario currentUser) {
        Optional<Libro> miCopia = libroService.buscarMiCopia(libroBase.getTitulo(), libroBase.getAutor(), currentUser);
        boolean yaLoTengo = miCopia.isPresent();
        Libro libroAMostrar = yaLoTengo ? miCopia.get() : libroBase;

        Component cover;
        if (libroBase.getUrlPortada() != null && !libroBase.getUrlPortada().isEmpty()) {
            Image img = new Image(libroBase.getUrlPortada(), "Portada de " + libroBase.getTitulo());
            img.setWidthFull();
            img.setHeight("180px");
            img.getStyle().set("object-fit", "cover").set("border-radius", "10px");
            cover = img;
        } else {
            Div placeholder = new Div(new Icon(VaadinIcon.BOOK));
            placeholder.setWidthFull();
            placeholder.setHeight("180px");
            placeholder.getStyle().set("background", "#e2e8f0").set("display", "flex")
                    .set("align-items", "center").set("justify-content", "center").set("border-radius", "10px");
            cover = placeholder;
        }
        cover.getStyle().set("cursor", "pointer");
        cover.getElement().addEventListener("click", e -> mostrarDetalles(libroAMostrar, currentUser));

        VerticalLayout card = new VerticalLayout();
        card.setWidth("240px");
        card.getStyle().set("background", "white").set("border-radius", "15px").set("box-shadow", "0 4px 10px rgba(0,0,0,0.1)");

        int estrellasVisuales = yaLoTengo ? miCopia.get().getPuntuacion() : libroService.obtenerMediaComunidad(libroBase.getTitulo(), libroBase.getAutor());
        HorizontalLayout stars = new HorizontalLayout();
        for (int i = 1; i <= 5; i++) {
            final int n = i;
            Icon s = (i <= estrellasVisuales) ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
            s.setSize("16px");
            String color = i <= estrellasVisuales ? (yaLoTengo ? "#eab308" : "#3b82f6") : "#e2e8f0";
            s.getStyle().set("color", color);

            if (yaLoTengo) {
                s.getStyle().set("cursor", "pointer");
                s.addClickListener(e -> {
                    miCopia.get().setPuntuacion(n);
                    libroService.guardarLibro(miCopia.get());
                    actualizarCatalogo();
                });
            }
            stars.add(s);
        }

        H4 t = new H4(libroBase.getTitulo());
        t.getStyle().set("cursor", "pointer").set("margin", "10px 0 0 0");
        t.addClickListener(e -> mostrarDetalles(libroAMostrar, currentUser));

        card.add(cover, t, new Span(libroBase.getAutor()));

        if (!yaLoTengo) {
            Button btnAdd = new Button("Añadir a mi lista", VaadinIcon.COPY.create(), e -> {
                Libro copia = new Libro();
                copia.setTitulo(libroBase.getTitulo());
                copia.setAutor(libroBase.getAutor());
                copia.setCategoria(libroBase.getCategoria());
                copia.setSinopsis(libroBase.getSinopsis());
                copia.setUrlPortada(libroBase.getUrlPortada());
                copia.setUsuario(currentUser);
                libroService.guardarLibro(copia);
                Notification.show("¡Añadido a tu colección!");
                actualizarCatalogo();
            });
            btnAdd.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            card.add(btnAdd);
        } else {
            Button btnStatus = new Button(miCopia.get().isLeido() ? "Leído ✅" : "Pendiente ⏳");
            btnStatus.addThemeVariants(miCopia.get().isLeido() ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_CONTRAST);
            btnStatus.getStyle().set("font-size", "0.75rem");
            btnStatus.addClickListener(e -> {
                miCopia.get().setLeido(!miCopia.get().isLeido());
                libroService.guardarLibro(miCopia.get());
                actualizarCatalogo();
            });
            card.add(btnStatus);
        }

        card.add(stars);
        card.setAlignItems(Alignment.CENTER);
        return card;
    }

    private void mostrarDetalles(Libro libro, Usuario currentUser) {
        Dialog d = new Dialog();
        d.setWidth("550px");
        d.setHeaderTitle(libro.getTitulo());

        VerticalLayout layout = new VerticalLayout();

        String genero = libro.getCategoria() != null ? libro.getCategoria().getNombre() : "General";
        int mediaGlobal = libroService.obtenerMediaComunidad(libro.getTitulo(), libro.getAutor());

        HorizontalLayout infoPanel = new HorizontalLayout(
                new Span("📂 Categoría: " + genero),
                new Span("⭐ Media Global: " + mediaGlobal + "/5")
        );
        infoPanel.getStyle().set("color", "#64748b").set("font-size", "0.85rem").set("font-weight", "bold");

        layout.add(infoPanel, new Html("<span><b>Sinopsis:</b></span>"), new Paragraph(libro.getSinopsis()));
        layout.add(new Hr(), new Html("<span><b>Opiniones de la comunidad:</b></span>"));

        VerticalLayout listaComentarios = new VerticalLayout();
        listaComentarios.setPadding(false);

        Runnable recargar = () -> {
            listaComentarios.removeAll();
            comentarioService.listarComunidad(libro.getTitulo(), libro.getAutor()).forEach(c -> {
                Div row = new Div(new Span("@" + c.getUsuario().getUsername() + ": "), new Span(c.getTexto()));
                row.getStyle().set("background", "#f1f5f9").set("padding", "8px").set("border-radius", "8px").set("margin-bottom", "5px").set("width", "100%");
                listaComentarios.add(row);
            });
        };
        recargar.run();

        TextArea input = new TextArea();
        input.setPlaceholder("Escribe tu opinión...");
        input.setWidthFull();

        Button btnComm = new Button("Comentar", e -> {
            String txt = input.getValue();
            if (txt == null || txt.trim().isEmpty()) { // VALIDACIÓN VACÍO
                Notification.show("⚠️ El comentario no puede estar vacío");
                return;
            }
            comentarioService.guardar(new Comentario(txt, currentUser, libro));
            input.clear();
            recargar.run();
        });
        btnComm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        layout.add(listaComentarios, input, btnComm);
        d.add(layout);
        d.getFooter().add(new Button("Cerrar", e -> d.close()));
        d.open();
    }

    private void abrirDialogoNuevoLibro() {
        Dialog d = new Dialog();
        d.setHeaderTitle("Nuevo Libro");
        TextField t = new TextField("Título");
        TextField a = new TextField("Autor");
        ComboBox<Categoria> c = new ComboBox<>("Género");
        c.setItems(categoriaService.listarTodas());
        c.setItemLabelGenerator(Categoria::getNombre);
        TextArea s = new TextArea("Sinopsis");

        d.add(new VerticalLayout(t, a, c, s));
        d.getFooter().add(new Button("Cancelar", x -> d.close()), new Button("Añadir", e -> {
            Libro n = new Libro();
            n.setTitulo(t.getValue()); n.setAutor(a.getValue());
            n.setCategoria(c.getValue()); n.setSinopsis(s.getValue());
            usuarioService.buscarPorNombre(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(n::setUsuario);
            libroService.guardarLibro(n);
            actualizarCatalogo();
            d.close();
        }));
        d.open();
    }
}