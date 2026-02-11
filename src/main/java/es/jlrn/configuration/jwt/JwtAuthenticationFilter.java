// // package es.jlrn.configuration.jwt;

// // import jakarta.servlet.FilterChain;
// // import jakarta.servlet.ServletException;
// // import jakarta.servlet.http.Cookie;
// // import jakarta.servlet.http.HttpServletRequest;
// // import jakarta.servlet.http.HttpServletResponse;
// // import lombok.RequiredArgsConstructor;
// // import org.springframework.http.HttpHeaders;
// // import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// // import org.springframework.security.core.context.SecurityContextHolder;
// // import org.springframework.security.core.userdetails.UserDetails;
// // import org.springframework.security.core.userdetails.UserDetailsService;
// // import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// // import org.springframework.stereotype.Component;
// // import org.springframework.web.filter.OncePerRequestFilter;

// // import java.io.IOException;

// // @Component
// // @RequiredArgsConstructor
// // public class JwtAuthenticationFilter extends OncePerRequestFilter {

// //     private final JwtService jwtService;
// //     private final UserDetailsService userDetailsService;

// //     @Override
// //     protected void doFilterInternal(HttpServletRequest request,
// //                                     HttpServletResponse response,
// //                                     FilterChain filterChain)
// //             throws ServletException, IOException {

// //         String jwt = null;

// //         // 1️⃣ Buscar JWT en Authorization header
// //         final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
// //         if (authHeader != null && authHeader.startsWith("Bearer ")) {
// //             jwt = authHeader.substring(7).trim();
// //         }

// //         // 2️⃣ Si no está, buscar en cookie "accessToken"
// //         if (jwt == null && request.getCookies() != null) {
// //             for (Cookie cookie : request.getCookies()) {
// //                 if ("accessToken".equals(cookie.getName())) {
// //                     jwt = cookie.getValue();
// //                     break;
// //                 }
// //             }
// //         }

// //         try {
// //             if (jwt != null) {
// //                 String username = jwtService.getUsernameFromToken(jwt);

// //                 if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
// //                     UserDetails userDetails = userDetailsService.loadUserByUsername(username);

// //                     if (jwtService.isTokenValid(jwt, userDetails)) {
// //                         UsernamePasswordAuthenticationToken authToken =
// //                                 new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
// //                         authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
// //                         SecurityContextHolder.getContext().setAuthentication(authToken);
// //                     }
// //                 }
// //             }

// //         } catch (Exception e) {
// //             // 🚨 JWT inválido o expirado → devolver 401 y terminar
// //             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
// //             response.getWriter().write("JWT Token invalid or expired.");
// //             return;
// //         }

// //         filterChain.doFilter(request, response);
// //     }
// // }



package es.jlrn.configuration.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = null;

        // 1. Intentar obtener del Header
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7).trim();
        }

        // 2. Si no hay, intentar obtener de la Cookie "accessToken"
        if (jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        // 3. Procesar el token si existe
        if (jwt != null) {
            try {
                String username = jwtService.getUsernameFromToken(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Aquí es donde Spring Security carga al usuario (incluyendo sus roles)
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Si el token es inválido o ha expirado, limpiamos el contexto
                SecurityContextHolder.clearContext();
                // Opcional: Podrías dejar que el filtro siga y el EntryPoint lance el 401
                // o mantener tu lógica de respuesta inmediata:
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expired or invalid\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
