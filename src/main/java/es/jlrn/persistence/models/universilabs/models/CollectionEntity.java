package es.jlrn.persistence.models.universilabs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Table(
    name = "collections",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "project_id"}) // Evita colecciones duplicadas en un mismo proyecto
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionEntity {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la colección no puede estar vacío")
    @Size(max = 255, message = "El nombre no puede tener más de 255 caracteres")
    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne(optional = false) // no puede existir colección sin proyecto
    @JoinColumn(name = "project_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Topic> topics;
}
