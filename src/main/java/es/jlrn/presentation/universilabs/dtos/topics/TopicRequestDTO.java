package es.jlrn.presentation.universilabs.dtos.topics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// TopicRequestDTO.java
public record TopicRequestDTO(
    @NotBlank(message = "El título del tema es obligatorio")
    @Size(max = 255)
    String title,

    @NotNull(message = "El ID de la colección es obligatorio")
    Long collectionId
) {}