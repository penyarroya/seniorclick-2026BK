package es.jlrn.persistence.models.universilabs.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "user_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgress {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El estado no puede estar vacío")
    @Pattern(regexp = "STARTED|COMPLETED", message = "El estado debe ser STARTED o COMPLETED")
    @Column(length = 20, nullable = false)
    private String status;

    @Min(value = 0, message = "El tiempo transcurrido no puede ser negativo")
    @Column(nullable = false)
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime lastAccess = LocalDateTime.now();

    // CAMBIO CLAVE: Ahora el progreso pertenece a una inscripción
    @ManyToOne(optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties({"user"})
    private Enrollment enrollment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "page_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Page page;

    @Column(columnDefinition = "TEXT") // TEXT por si el mensaje es largo
    private String motivationMessage;
}

