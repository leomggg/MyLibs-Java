package com.example.MyLibs.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Bienvenido | MyLibs")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f8fafc");

        VerticalLayout container = new VerticalLayout();
        container.setWidth("auto");
        container.setPadding(true);
        container.getStyle().set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 6px -1px rgb(0 0 0 / 0.1)");

        H1 titulo = new H1("MyLibs");
        titulo.getStyle().set("color", "#0f172a");

        Paragraph sub = new Paragraph("Tu diario personal de lectura");
        sub.getStyle().set("margin-top", "-10px").set("color", "#64748b");

        login.setAction("login");

        container.add(titulo, sub, login);
        container.setAlignItems(Alignment.CENTER);

        add(container);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}