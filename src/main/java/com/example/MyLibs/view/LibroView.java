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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

@Route("")
@PageTitle("MyLibs | Comunidad Literaria")
@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
public class LibroView extends VerticalLayout {

    private final LibroService libroService;
    private final CategoriaService categoriaService;
    private final UsuarioService usuarioService;
    private final ComentarioService comentarioService;
    private final transient AuthenticationContext authContext;

    private FlexLayout catalogLayout = new FlexLayout();
    private TextField searchField = new TextField();
    private Tab allTab = new Tab(VaadinIcon.GLOBE.create(), new Span(" Muro Global"));
    private Tab readTab = new Tab(VaadinIcon.CHECK_CIRCLE.create(), new Span(" Mis Leídos"));
    private Tab pendingTab = new Tab(VaadinIcon.CLOCK.create(), new Span(" Pendientes"));
    private Tabs filters = new Tabs(allTab, readTab, pendingTab);

    public LibroView(LibroService ls, CategoriaService cs, UsuarioService us, ComentarioService cms, AuthenticationContext authContext) {
        this.libroService = ls; this.categoriaService = cs;
        this.usuarioService = us; this.comentarioService = cms;
        this.authContext = authContext;

        setSizeFull();
        setPadding(false);
        getStyle().set("background-color", "#FDFBF7");

        // --- BARRA SUPERIOR ---
        H1 logo = new H1("MyLibs");
        logo.getStyle().set("font-family", "serif").set("color", "#2D5A27").set("margin", "0").set("font-size", "1.8rem");

        searchField.setPlaceholder("Título, autor o género...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> actualizarCatalogo());

        Button btnLogout = new Button("Salir", VaadinIcon.EXIT.create(), e -> authContext.logout());
        btnLogout.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout header = new HorizontalLayout(logo, filters, searchField, btnLogout);
        header.setWidthFull(); header.setAlignItems(Alignment.CENTER); header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.getStyle().set("background", "white").set("padding", "10px 30px").set("box-shadow", "0 2px 8px rgba(0,0,0,0.05)");

        // --- BOTÓN FLOTANTE (+) ---
        Button btnAdd = new Button(VaadinIcon.PLUS.create(), e -> abrirDialogoNuevoLibro());
        btnAdd.getStyle()
                .set("position", "fixed").set("bottom", "30px").set("right", "30px")
                .set("background-color", "#2D5A27").set("color", "white")
                .set("border-radius", "50%").set("width", "70px").set("height", "70px")
                .set("box-shadow", "0 10px 30px rgba(45, 90, 39, 0.4)");

        filters.addSelectedChangeListener(e -> actualizarCatalogo());
        catalogLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        catalogLayout.getStyle().set("gap", "25px").set("padding", "40px");

        add(header, catalogLayout, btnAdd);
        actualizarCatalogo();
    }

    private void actualizarCatalogo() {
        catalogLayout.removeAll();
        String currentName = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario currentUser = usuarioService.buscarPorNombre(currentName).orElse(null);
        String term = searchField.getValue().toLowerCase().trim();

        List<Libro> filtrados = libroService.listarTodos().stream()
                .filter(l -> term.isEmpty() || l.getTitulo().toLowerCase().contains(term) ||
                        l.getAutor().toLowerCase().contains(term) ||
                        (l.getCategoria() != null && l.getCategoria().getNombre().toLowerCase().contains(term)))
                .collect(Collectors.toList());

        if (filters.getSelectedTab().equals(allTab)) {
            // Unicidad en el muro
            Collection<Libro> unicos = filtrados.stream().collect(Collectors.toMap(
                    l -> (l.getTitulo() + l.getAutor()).toLowerCase(), l -> l, (a, b) -> a)).values();
            unicos.forEach(l -> catalogLayout.add(crearCard(l, currentUser)));
        } else {
            filtrados.stream()
                    .filter(l -> l.getUsuario().getUsername().equals(currentName))
                    .filter(l -> filters.getSelectedTab().equals(readTab) ? l.isLeido() : !l.isLeido())
                    .forEach(l -> catalogLayout.add(crearCard(l, currentUser)));
        }
    }

    private Component crearCard(Libro libroBase, Usuario currentUser) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("250px");
        card.getStyle().set("background", "white").set("border-radius", "20px")
                .set("box-shadow", "0 8px 20px rgba(0,0,0,0.06)").set("padding", "20px")
                .set("transition", "transform 0.2s");

        card.getElement().addEventListener("mouseover", e -> card.getStyle().set("transform", "translateY(-10px)"));
        card.getElement().addEventListener("mouseout", e -> card.getStyle().set("transform", "translateY(0)"));

        Optional<Libro> miCopia = libroService.buscarMiCopia(libroBase.getTitulo(), libroBase.getAutor(), currentUser);
        boolean yaLoTengo = miCopia.isPresent();
        Libro libroAMostrar = yaLoTengo ? miCopia.get() : libroBase;

        // Foto de portada
        Component cover;
        if (libroBase.getUrlPortada() != null && !libroBase.getUrlPortada().isEmpty()) {
            Image img = new Image(libroBase.getUrlPortada(), "Portada");
            img.setWidthFull(); img.setHeight("200px");
            img.getStyle().set("object-fit", "cover").set("border-radius", "12px");
            cover = img;
        } else {
            Div placeholder = new Div(new Icon(VaadinIcon.BOOK));
            placeholder.setWidthFull(); placeholder.setHeight("200px");
            placeholder.getStyle().set("background", "#f1f5f9").set("display", "flex")
                    .set("align-items", "center").set("justify-content", "center").set("border-radius", "12px");
            cover = placeholder;
        }
        cover.getStyle().set("cursor", "pointer");
        cover.getElement().addEventListener("click", e -> mostrarDetalles(libroAMostrar, currentUser));

        // Estrellas por colores
        int starsVal = yaLoTengo ? miCopia.get().getPuntuacion() : libroService.obtenerMediaComunidad(libroBase.getTitulo(), libroBase.getAutor());
        HorizontalLayout stars = new HorizontalLayout();
        for (int i = 1; i <= 5; i++) {
            final int n = i;
            Icon s = (i <= starsVal) ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
            s.setSize("16px");
            s.getStyle().set("color", i <= starsVal ? (yaLoTengo ? "#eab308" : "#3b82f6") : "#e2e8f0");
            if (yaLoTengo) {
                s.getStyle().set("cursor", "pointer");
                s.addClickListener(e -> { miCopia.get().setPuntuacion(n); libroService.guardarLibro(miCopia.get()); actualizarCatalogo(); });
            }
            stars.add(s);
        }

        H3 t = new H3(libroBase.getTitulo());
        t.getStyle().set("font-family", "serif").set("margin", "15px 0 2px 0").set("cursor", "pointer");
        t.addClickListener(e -> mostrarDetalles(libroAMostrar, currentUser));

        card.add(cover, t, new Span(libroBase.getAutor()), stars);

        if (!yaLoTengo) {
            Button btnAdd = new Button("Añadir a mi lista", e -> {
                Libro copia = new Libro();
                copia.setTitulo(libroBase.getTitulo()); copia.setAutor(libroBase.getAutor());
                copia.setCategoria(libroBase.getCategoria()); copia.setSinopsis(libroBase.getSinopsis());
                copia.setUrlPortada(libroBase.getUrlPortada()); copia.setUsuario(currentUser);
                libroService.guardarLibro(copia); actualizarCatalogo();
                Notification.show("¡Libro guardado!");
            });
            btnAdd.getStyle().set("background-color", "#2D5A27").set("color", "white").set("border-radius", "10px");
            card.add(btnAdd);
        } else {
            Button btnStatus = new Button(miCopia.get().isLeido() ? "Leído ✅" : "Pendiente ⏳");
            btnStatus.addThemeVariants(miCopia.get().isLeido() ? ButtonVariant.LUMO_SUCCESS : ButtonVariant.LUMO_CONTRAST);
            btnStatus.addClickListener(e -> {
                miCopia.get().setLeido(!miCopia.get().isLeido());
                libroService.guardarLibro(miCopia.get()); actualizarCatalogo();
            });
            card.add(btnStatus);
        }

        card.setAlignItems(Alignment.CENTER);
        return card;
    }

    private void mostrarDetalles(Libro libro, Usuario currentUser) {
        Dialog d = new Dialog();
        d.setWidth("550px");
        d.setHeaderTitle(libro.getTitulo());

        VerticalLayout layout = new VerticalLayout();

        HorizontalLayout info = new HorizontalLayout(
                new Span("📂 " + (libro.getCategoria() != null ? libro.getCategoria().getNombre() : "General")),
                new Span("⭐ Comunidad: " + libroService.obtenerMediaComunidad(libro.getTitulo(), libro.getAutor()) + "/5")
        );
        info.getStyle().set("color", "#64748b").set("font-size", "0.9rem").set("font-weight", "bold");

        VerticalLayout commentsList = new VerticalLayout();
        commentsList.setPadding(false);
        Runnable reload = () -> {
            commentsList.removeAll();
            comentarioService.listarComunidad(libro.getTitulo(), libro.getAutor()).forEach(c -> {
                Div row = new Div(new Span("@" + c.getUsuario().getUsername() + ": "), new Span(c.getTexto()));
                row.getStyle().set("background", "#f1f5f9").set("padding", "10px").set("border-radius", "10px").set("width", "100%");
                commentsList.add(row);
            });
        };
        reload.run();

        TextArea input = new TextArea();
        input.setPlaceholder("Escribe tu opinión..."); input.setWidthFull();

        Button btnComm = new Button("Publicar Opinión", e -> {
            if (input.getValue().trim().isEmpty()) return;
            comentarioService.guardar(new Comentario(input.getValue(), currentUser, libro));
            input.clear(); reload.run();
        });
        btnComm.getStyle().set("background-color", "#2D5A27").set("color", "white");

        layout.add(info, new Html("<span><b>Sinopsis:</b></span>"), new Paragraph(libro.getSinopsis()), new Hr(), new Html("<span><b>Opiniones:</b></span>"), commentsList, input, btnComm);
        d.add(layout);
        d.getFooter().add(new Button("Cerrar", e -> d.close()));
        d.open();
    }

    private void abrirDialogoNuevoLibro() {
        Dialog d = new Dialog();
        d.setHeaderTitle("Nuevo Libro en la Comunidad");
        TextField t = new TextField("Título");
        TextField a = new TextField("Autor");
        ComboBox<Categoria> c = new ComboBox<>("Género");
        c.setItems(categoriaService.listarTodas());
        c.setItemLabelGenerator(Categoria::getNombre);
        TextArea s = new TextArea("Sinopsis");

        Button save = new Button("Publicar", e -> {
            Libro n = new Libro(); n.setTitulo(t.getValue()); n.setAutor(a.getValue());
            n.setCategoria(c.getValue()); n.setSinopsis(s.getValue());
            usuarioService.buscarPorNombre(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(n::setUsuario);
            libroService.guardarLibro(n); actualizarCatalogo(); d.close();
        });
        save.getStyle().set("background-color", "#2D5A27").set("color", "white");

        d.add(new VerticalLayout(t, a, c, s));
        d.getFooter().add(new Button("Cancelar", x -> d.close()), save);
        d.open();
    }
}