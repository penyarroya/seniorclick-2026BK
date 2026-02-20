package es.jlrn.persistence.models.universilabs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
    name = "topics",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "collection_id"})
    }
)
public class Topic {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del tema no puede estar vacío")
    @Size(max = 255, message = "El título del tema no puede exceder los 255 caracteres")
    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(optional = false)
    @JoinColumn(name = "collection_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CollectionEntity collection;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Subtopic> subtopics;
}
