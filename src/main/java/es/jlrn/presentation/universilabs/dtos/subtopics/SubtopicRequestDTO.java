package es.jlrn.presentation.universilabs.dtos.subtopics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubtopicRequestDTO(
//    
    @NotBlank(message = "El título es obligatorio")
    String title,
    @NotNull(message = "El ID del tema es obligatorio")
    Long topicId
) {}