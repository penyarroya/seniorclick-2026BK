package es.jlrn.presentation.universilabs.dtos.projects;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDTO {
//
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede superar los 255 caracteres")
    private String title;

    @Size(max = 5000, message = "La descripción es demasiado larga")
    private String description;

    @Min(value = 1, message = "El nivel mínimo es 1")
    @Max(value = 3, message = "El nivel máximo es 3")
    private Integer level;

    @NotNull(message = "La institución es obligatoria")
    private Long institutionId;

    @NotNull(message = "El ID del creador es obligatorio")
    private Long createdById;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;
}