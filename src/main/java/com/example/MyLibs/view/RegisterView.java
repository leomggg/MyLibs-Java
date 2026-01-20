package com.example.MyLibs.view;

import com.example.MyLibs.entities.Usuario;
import com.example.MyLibs.services.UsuarioService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("register")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    public RegisterView(UsuarioService userService, PasswordEncoder encoder) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        TextField username = new TextField("Nombre de usuario");
        PasswordField password = new PasswordField("Contraseña");
        Button btnRegister = new Button("Crear Cuenta", e -> {
            if (userService.buscarPorNombre(username.getValue()).isPresent()) {
                Notification.show("Ese usuario ya existe");
            } else {
                Usuario nuevo = new Usuario();
                nuevo.setUsername(username.getValue());
                nuevo.setPassword(encoder.encode(password.getValue()));
                nuevo.setEnabled(true);
                userService.registrarUser(nuevo);
                Notification.show("¡Cuenta creada! Ya puedes iniciar sesión");
                getUI().ifPresent(ui -> ui.navigate("login"));
            }
        });

        add(new H1("Regístrate en MyLibs"), username, password, btnRegister);
    }
}