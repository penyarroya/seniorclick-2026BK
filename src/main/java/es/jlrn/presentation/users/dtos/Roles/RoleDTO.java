package es.jlrn.presentation.users.dtos.Roles;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
//
    private Long id;            // ID del rol
    private String name;        // Nombre del rol (ej: ROLE_ADMIN)
    private Set<String> permissions; // Lista de nombres de permisos
}
