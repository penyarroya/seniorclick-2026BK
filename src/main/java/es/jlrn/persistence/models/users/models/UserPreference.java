package es.jlrn.persistence.models.users.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import es.jlrn.persistence.enums.Theme;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "preferences")
public class UserPreference {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId; // 0L = preferencias globales

    @Min(0)
    @Column(name = "selected_institution_id")
    private Integer selectedInstitutionId;

    @Size(max = 255)
    @Column(name = "last_page", length = 255)
    private String lastPage;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme", length = 10, nullable = false)
    private Theme theme;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // 🔹 Defaults y normalización
    @PrePersist
    @PreUpdate
    private void applyDefaultsAndNormalize() {
        if (theme == null) theme = Theme.LIGHT;
        if (lastPage == null || lastPage.isBlank()) lastPage = "/";
        else lastPage = lastPage.trim();
    }
}
