package es.jlrn.presentation.universilabs.dtos.resources;

import es.jlrn.persistence.enums.ResourceType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceDTO {
//
    private Long id;

    @NotBlank(message = "El título del recurso es obligatorio")
    @Size(max = 255, message = "El título no puede exceder los 255 caracteres")
    private String title;

    @NotNull(message = "El tipo de recurso (IMAGE, VIDEO, etc.) es obligatorio")
    private ResourceType type;

    @NotBlank(message = "La URL del recurso es obligatoria")
    private String url;

    private Integer order;

    @NotNull(message = "El ID de la página es obligatorio")
    private Long pageId;

    // Campos de auditoría opcionales para lectura
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}