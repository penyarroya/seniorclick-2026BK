package es.jlrn.persistence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum Permission {
//
    // Usuarios
    USER_READ,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE,
    USER_PASSWORD_CHANGE,
    USER_ASSIGN_ROLE,

    // Roles y permisos
    ROLE_MANAGE,
    PERMISSION_MANAGE,

    // Posts
    POST_READ,
    POST_CREATE,
    POST_UPDATE,
    POST_DELETE,
    POST_COMMENT,

    // Reportes
    REPORT_VIEW,
    REPORT_EXPORT,

    // Administración
    ADMIN_PANEL_ACCESS,
    SYSTEM_CONFIGURE;

    // Grupo de permisos frecuentes para roles de solo lectura
    public static Set<Permission> readOnly() {
        return EnumSet.of(USER_READ, POST_READ, REPORT_VIEW);
    }
}
