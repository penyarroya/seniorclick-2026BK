package es.jlrn.persistence.models.users.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class RoleEntity {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del rol no puede estar vacío.")
    @Size(max = 50, message = "El nombre del rol no puede exceder los 50 caracteres.")
    @Column(nullable = false, unique = true, length = 50)
    private String name; // Ej: ROLE_ADMIN, ROLE_USER

    @NotEmpty(message = "El rol debe tener al menos un permiso.")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<PermissionEntity> permissions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleEntity)) return false;
        return id != null && id.equals(((RoleEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
