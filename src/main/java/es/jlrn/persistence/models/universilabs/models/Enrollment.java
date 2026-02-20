package es.jlrn.persistence.models.universilabs.models;

import es.jlrn.persistence.models.users.models.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = { "user_id", "project_id" },
                        name = "uk_user_project_enrollment"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
//
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El rol no puede estar vacío")
    @Pattern(
            regexp = "LEARNER|MENTOR|COLLABORATOR",
            message = "El rol debe ser LEARNER, MENTOR o COLLABORATOR"
    )
    @Column(nullable = false, length = 30)
    private String roleInCourse;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) 
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties({"roles", "password", "authorities", "verificationCode", "passwordResetToken"})
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Project project;

    // --- NUEVA RELACIÓN CON CASCADA ---
    @OneToMany(
        mappedBy = "enrollment", 
        cascade = CascadeType.ALL, 
        orphanRemoval = true
    )
    @Builder.Default
    @JsonIgnore // Evita que al cargar Enrollment se cargue todo el progreso si no lo necesitas
    private List<UserProgress> progresses = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Integer progressPercentage = 0; // Añade esta línea
}
