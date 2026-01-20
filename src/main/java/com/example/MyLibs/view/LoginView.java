package com.example.MyLibs.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Acceso | MyLibs")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f8fafc");

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        Button btnRegister = new Button("¿No tienes cuenta? Regístrate aquí", e -> {
            getUI().ifPresent(ui -> ui.navigate("register"));
        });
        btnRegister.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout card = new VerticalLayout(new H1("MyLibs"), login, btnRegister);
        card.setWidth("auto");
        card.setAlignItems(Alignment.CENTER);
        card.getStyle().set("background", "white").set("padding", "2em").set("border-radius", "15px")
                .set("box-shadow", "0 10px 25px rgba(0,0,0,0.1)");

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}