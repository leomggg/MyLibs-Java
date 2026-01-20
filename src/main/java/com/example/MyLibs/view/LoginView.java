package com.example.MyLibs.view;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
@PageTitle("Entrar | MyLibs")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#FDFBF7"); // Fondo crema papel

        // --- INYECCIÓN DE ESTILO PARA EL BOTÓN DE LOGIN ---
        Html customStyle = new Html("<style>" +
                "vaadin-login-form-wrapper vaadin-button[theme~='primary'] {" +
                "   background-color: #2D5A27 !important;" +
                "   color: white !important;" +
                "   border-radius: 12px !important;" +
                "   font-weight: bold !important;" +
                "   cursor: pointer !important;" +
                "}" +
                "vaadin-login-form-wrapper vaadin-text-field, vaadin-login-form-wrapper vaadin-password-field {" +
                "   border-radius: 10px !important;" +
                "}" +
                "</style>");

        H1 logo = new H1("MyLibs");
        logo.getStyle().set("font-family", "'Serif', 'Georgia'").set("color", "#2D5A27").set("font-size", "3.5rem");

        Paragraph slogan = new Paragraph("Tu refugio literario personal y compartido");
        slogan.getStyle().set("color", "#64748b").set("margin-top", "-15px").set("font-style", "italic");

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        Button btnRegister = new Button("¿Aún no tienes cuenta? Crea una aquí", e -> {
            getUI().ifPresent(ui -> ui.navigate("register"));
        });
        btnRegister.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnRegister.getStyle().set("color", "#2D5A27").set("font-weight", "bold").set("margin-top", "10px");

        VerticalLayout card = new VerticalLayout(logo, slogan, login, btnRegister);
        card.setAlignItems(Alignment.CENTER);
        card.setWidth("450px");
        card.getStyle()
                .set("background", "white")
                .set("padding", "50px")
                .set("border-radius", "30px")
                .set("box-shadow", "0 25px 60px rgba(0,0,0,0.08)");

        add(customStyle, card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}