package es.jlrn.persistence.models.universilabs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "institutions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_institution_email",
                        columnNames = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id") // <--- ESTO SOLUCIONA LA RECURSIVIDAD
public class Institution {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la institución no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede tener más de 255 caracteres")
    @Column(nullable = false, length = 255)
    private String name;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 500, message = "La dirección no puede tener más de 500 caracteres")
    @Column(nullable = false, length = 500)
    private String address;

    @Pattern(
            regexp = "^(https?://)?([\\w\\d-]+\\.)+[\\w-]+(/[\\w\\d#?=&%+.-]*)?$",
            message = "El sitio web debe ser una URL válida"
    )
    @Size(max = 500, message = "El sitio web no puede tener más de 500 caracteres")
    @Column(length = 500)
    private String website;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe tener un formato válido")
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Cambia tu bloque de projects por este:
    @OneToMany(mappedBy = "institution", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Project> projects;
}
