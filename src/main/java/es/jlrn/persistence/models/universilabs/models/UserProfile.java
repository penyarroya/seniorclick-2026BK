package es.jlrn.persistence.models.universilabs.models;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import es.jlrn.persistence.models.users.models.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
//
    @Id
    private Long userId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) 
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonBackReference // Evita recursividad infinita
    private UserEntity user;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(length = 100)
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    @Column(length = 100)
    private String lastName;

    @Pattern(
        regexp = "^$|^(\\+\\d{1,3}[- ]?)?\\d{7,15}$",
        message = "El teléfono debe ser válido"
    )
    @Column(length = 20)
    private String phone;

    @Size(max = 500, message = "La URL del avatar no puede exceder los 500 caracteres")
    @Pattern(
        // Acepta: vacío OR (cualquier ruta que empiece por http, assets, o carpetas estándar)
        regexp = "^$|^(https?://|/|[a-zA-Z0-9_-]+/).*$",
        message = "La URL o ruta local del avatar no es válida"
    )
    @Column(length = 500)
    private String avatarUrl;

    @PrePersist
    @PreUpdate
    public void preClean() {
        if (this.phone != null && this.phone.isEmpty()) this.phone = null;
        if (this.avatarUrl != null && this.avatarUrl.isEmpty()) this.avatarUrl = null;
    }
}
