// package es.jlrn.configuration.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;

// /**
//  * Clase de configuración para definir los beans clave de Spring Security.
//  * Se ha eliminado la anotación @RequiredArgsConstructor por no ser necesaria.
//  */
// @Configuration
// public class ApplicationConfig {
// //
//     // El campo final ha sido eliminado para romper el ciclo de dependencia.

//     /**
//      * Define el bean para el codificador de contraseñas (BCrypt).
//      */
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     /**
//      * Define explícitamente el AuthenticationProvider.
//      * Inyectamos UserDetailsService como parámetro del método.
//      */
//     @Bean
//     public AuthenticationProvider authenticationProvider(
//         UserDetailsService userDetailsService) { // <-- Inyección por parámetro

//         // Uso del constructor específico
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
            
//         // 1. Asigna el codificador de contraseñas.
//         authProvider.setPasswordEncoder(passwordEncoder()); 
        
//         // 2. No ocultar la excepción si el usuario no es encontrado
//         authProvider.setHideUserNotFoundExceptions(false); 

//         return authProvider;
//     }

//     /**
//      * Define el bean AuthenticationManager.
//      */
//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//         return config.getAuthenticationManager();
//     }
// }

package es.jlrn.configuration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de beans clave de Spring Security:
 * - PasswordEncoder
 * - AuthenticationProvider
 * - AuthenticationManager
 */
@Configuration
public class ApplicationConfig {

    /**
     * Bean para codificar contraseñas con BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean para el AuthenticationProvider usando tu versión moderna de DaoAuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        // Usa el constructor moderno que inyecta UserDetailsService
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        
        // Codificador de contraseñas
        authProvider.setPasswordEncoder(passwordEncoder());
        
        // No ocultar excepciones si el usuario no es encontrado
        authProvider.setHideUserNotFoundExceptions(false);

        return authProvider;
    }

    /**
     * Bean para AuthenticationManager, necesario para la autenticación manual (login JWT, etc.)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
