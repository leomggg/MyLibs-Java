package com.example.MyLibs.config;

import com.example.MyLibs.security.JWTAuthorizationFilter;
import com.example.MyLibs.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final JWTAuthorizationFilter jwtFilter;

    public SecurityConfig(JWTAuthorizationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 1. Permitir acceso a la API REST y Consola H2
        // IMPORTANTE: NO pongas .anyRequest() aquí, deja que Vaadin lo gestione al final.
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/data/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
        );

        // 2. Desactivar CSRF solo para API y H2
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(
                        new AntPathRequestMatcher("/api/**"),
                        new AntPathRequestMatcher("/h2-console/**")
                )
        );

        // 3. Configuración para frames (Consola H2)
        http.headers(headers -> headers.frameOptions(f -> f.disable()));

        // 4. Añadir el filtro JWT antes del de usuario/password
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // 5. Configuración base de Vaadin (Añade sus rutas y el anyRequest().authenticated() final)
        super.configure(http);

        // 6. Registrar la vista de login para la redirección automática
        setLoginView(http, LoginView.class);
    }
}