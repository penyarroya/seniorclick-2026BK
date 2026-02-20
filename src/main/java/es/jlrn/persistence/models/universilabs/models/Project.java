package es.jlrn.persistence.models.universilabs.models;

import es.jlrn.persistence.models.users.models.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "projects",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = { "title", "institution_id" },
                        name = "uk_project_title_institution"
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
public class Project {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del proyecto no puede estar vacío")
    @Size(max = 255, message = "El título no puede exceder los 255 caracteres")
    @Column(nullable = false, length = 255)
    private String title;

    @Size(max = 5000, message = "El resumen no puede exceder los 5000 caracteres")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(value = 1, message = "El nivel mínimo de dificultad es 1")
    @Max(value = 3, message = "El nivel máximo de dificultad es 3")
    @Column(nullable = false)
    @Builder.Default
    private int level = 1; // Nivel de dificultad por defecto

    // Institución propietaria
    @ManyToOne(optional = false)
    @JoinColumn(name = "institution_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Institution institution;

    // Usuario que creó el proyecto
    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) 
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity createdBy;

    // Cambia tu bloque de enrollments por este:
    @JsonIgnore
    @OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Enrollment> enrollments;

    // Para las colecciones, podrías mantener ALL si consideras que 
    // las colecciones son parte inseparable del proyecto:
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollectionEntity> collections;

    // Dentro de la clase Project
    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    // Auditoría automática
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
