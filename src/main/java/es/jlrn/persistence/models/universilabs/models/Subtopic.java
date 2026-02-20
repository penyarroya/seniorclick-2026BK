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
    name = "subtopics",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "topic_id"})
    }
)
public class Subtopic {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del subtema no puede estar vacío")
    @Size(max = 255, message = "El título del subtema no puede exceder los 255 caracteres")
    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Topic topic;

    @OneToMany(mappedBy = "subtopic", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Page> pages;
}
