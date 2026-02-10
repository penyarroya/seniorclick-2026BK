package es.jlrn.presentation.universilabs.dtos.pages;

import es.jlrn.persistence.enums.PageFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PageRequestDTO(
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255)
    String title,

    // Añadimos el formato
    @NotNull(message = "El formato de página es obligatorio")
    PageFormat format, 

    @NotBlank(message = "El contenido es obligatorio")
    // Eliminamos o aumentamos el @Size si vas a permitir HTML pesado
    String content,

    @NotNull(message = "El ID del subtema es obligatorio")
    Long subtopicId,

    @NotNull(message = "El ID del autor es obligatorio")
    Long authorId
) {}

// public record PageRequestDTO(
//     @NotBlank(message = "El título es obligatorio") // Nuevo
//     @Size(max = 255)
//     String title,

//     @NotBlank(message = "El contenido es obligatorio")
//     @Size(max = 10000)
//     String content,

//     @NotNull(message = "El ID del subtema es obligatorio")
//     Long subtopicId,

//     @NotNull(message = "El ID del autor es obligatorio")
//     Long authorId
// ) {}