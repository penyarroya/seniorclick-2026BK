package es.jlrn.configuration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.jlrn.configuration.jwt.JwtAuthenticationFilter;
import es.jlrn.configuration.jwt.JwtEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtTokenFilter;
    private final JwtEntryPoint jwtEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtTokenFilter, JwtEntryPoint jwtEntryPoint) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtEntryPoint = jwtEntryPoint;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults()) // Configuración CORS
            .csrf(AbstractHttpConfigurer::disable) // APIs REST no usan CSRF
            .authorizeHttpRequests(auth -> auth
                // -------------------- RUTAS PÚBLICAS --------------------
                .requestMatchers(
                        "/auth/login",
                        "/auth/register",
                        "/auth/health",
                        "/auth/refresh-token",
                        "/auth/forgot-password",
                        "/auth/reset-password",
                        // IMPORTANTE: Esta es la ruta GET que el usuario pulsa en el email
                        "/auth/reset-link-confirmation", 
                        "/auth/verify-email",
                        "/auth/check-user",
                        "/auth/session",
                        "/auth/forgot-password/cancel",
                        "/api/roles",
                        "/api/projects/**",
                        "/api/users/**",
                        "/api/universilabs/**",
                        "/api/enrollments/**",
                        "/api/profiles/**",
                        "/api/progress/**",
                        "/api/collections/**",
                        "/api/topics/**",
                        "/api/subtopics/**",
                        "/api/pages/**",
                        "/api/resources/**",
                        "/api/user-progress/**"
                ).permitAll()

                // -------------------- RUTAS PROTEGIDAS --------------------
                .requestMatchers(
                        "/auth/me",
                        "/auth/logout",
                        "/auth/profile" // <--- Agregada aquí para que requiera estar logueado
                ).authenticated()

                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtEntryPoint) // Manejo de 401
            )
            // Insertamos nuestro filtro JWT antes del de usuario/contraseña
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // @Bean
    // protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .cors(Customizer.withDefaults()) // Configuración CORS
    //         .csrf(AbstractHttpConfigurer::disable) // APIs REST no usan CSRF
    //         .authorizeHttpRequests(auth -> auth
    //             // Rutas públicas (incluimos /auth/session para que no genere 401)
    //             .requestMatchers(
    //                     "/auth/login",
    //                     "/auth/register",
    //                     "/auth/health",
    //                     "/auth/forgot-password",
    //                     "/auth/reset-password",
    //                     "/auth/reset-password-by-token",
    //                     "/auth/verify-email",
    //                     "/auth/check-user",
    //                     "/auth/session",
    //                     "/auth/forgot-password/cancel",
    //                     "/api/roles",
    //                     "/api/projects/**"
    //             ).permitAll()

    //             // Rutas protegidas
    //             .requestMatchers(
    //                     "/auth/me",
    //                     "/auth/logout",
    //                     "/auth/refresh-token",
    //                     "/auth/profile"
    //             ).authenticated()

    //             .requestMatchers(
    //                 "/api/users/**",
    //                 "/api/universilabs/**"
    //             ).permitAll()

    //             .requestMatchers(
    //                 "/api/enrollments/**",   // Añadir para las Inscripciones
    //                 "/api/profiles/**",      // Añadir para UserProfile
    //                 "/api/progress/**" ,
    //                 "/api/collections/**" ,     // Añadir para UserProgress
    //                 "/api/topics/**",
    //                 "/api/subtopics/**",
    //                 "/api/pages/**",
    //                 "/api/resources/**",
    //                 "/api/user-progress/**"
    //             ).permitAll()

    //             // Cualquier otra ruta requiere autenticación
    //             .anyRequest().authenticated()
    //         )
    //         .exceptionHandling(exception -> exception
    //             .authenticationEntryPoint(jwtEntryPoint) // Manejo de 401
    //         )
    //         .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

    //     return http.build();
    // }


}
