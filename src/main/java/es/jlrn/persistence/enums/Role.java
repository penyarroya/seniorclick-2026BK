package es.jlrn.persistence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public enum Role {
//
    // SUPER_ADMIN tiene todos los permisos
    SUPER_ADMIN(EnumSet.allOf(Permission.class)),

    // ADMIN tiene permisos administrativos y sobre usuarios y posts
    ADMIN(EnumSet.of(
            Permission.USER_READ, Permission.USER_CREATE, Permission.USER_UPDATE, Permission.USER_DELETE,
            Permission.POST_READ, Permission.POST_CREATE, Permission.POST_UPDATE, Permission.POST_DELETE,
            Permission.REPORT_VIEW, Permission.REPORT_EXPORT,
            Permission.ADMIN_PANEL_ACCESS
    )),

    // USER tiene solo permisos de lectura
    USER(Permission.readOnly()),

    // MODERATOR puede leer, actualizar y comentar posts
    MODERATOR(EnumSet.of(
            Permission.POST_READ, Permission.POST_UPDATE, Permission.POST_COMMENT
    )),

    // MANAGER puede supervisar usuarios, posts y reportes
    MANAGER(EnumSet.of(
            Permission.USER_READ, Permission.POST_READ, Permission.REPORT_VIEW
    ));

    private final Set<Permission> defaultPermissions;

    /**
     * Devuelve el nombre del rol en formato compatible con Spring Security: "ROLE_<ROLE_NAME>"
     */
    public String getRoleName() {
        return "ROLE_" + this.name();
    }
}
