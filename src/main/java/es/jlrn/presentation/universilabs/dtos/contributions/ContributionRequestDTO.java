package es.jlrn.presentation.universilabs.dtos.contributions;

import jakarta.validation.constraints.*;

public record ContributionRequestDTO(
//    
    @NotBlank(message = "El contenido de la contribución es obligatorio")
    @Size(max = 10000)
    String content,
    
    @NotNull(message = "El ID de la página es obligatorio")
    Long pageId,
    
    @NotNull(message = "El ID del usuario es obligatorio")
    Long userId
    
) {}