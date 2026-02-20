package es.jlrn.presentation.users.services.interfaces;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación personalizada para restringir el acceso únicamente
 * a usuarios con la autoridad 'SUPER_ADMIN'.
 */
@Target({ElementType.METHOD, ElementType.TYPE}) // Se puede usar en métodos o en toda la clase
@Retention(RetentionPolicy.RUNTIME)           // Debe estar disponible en tiempo de ejecución
@PreAuthorize("hasAuthority('SUPER_ADMIN')")  // La lógica que ya probamos que funciona
public @interface IsSuperAdmin {
}