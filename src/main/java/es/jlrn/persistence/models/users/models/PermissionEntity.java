package es.jlrn.persistence.models.users.models;

import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permissions")
public class PermissionEntity {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del permiso no puede estar vacío.")
    @Size(max = 100, message = "El nombre del permiso no puede exceder los 100 caracteres.")
    @Column(nullable = false, unique = true, length = 100)
    private String name; // Ej: READ_USERS, WRITE_USERS

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionEntity)) return false;
        return id != null && id.equals(((PermissionEntity) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
