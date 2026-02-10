package es.jlrn.presentation.universilabs.dtos.usersprogress;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserProgressRequestDTO(
//    
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "STARTED|COMPLETED")
    String status,
    
    @Min(0)
    Integer timeSpentSeconds,
    
    @NotNull(message = "ID de usuario obligatorio")
    Long userId,
    
    @NotNull(message = "ID de página obligatorio")
    Long pageId
) {}