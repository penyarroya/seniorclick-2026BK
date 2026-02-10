package es.jlrn.presentation.universilabs.dtos.comments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequestDTO(
//    
    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(max = 5000, message = "El comentario no puede exceder los 5000 caracteres")
    String content,

    @NotNull(message = "El ID de la página es obligatorio")
    Long pageId,

    @NotNull(message = "El ID del usuario es obligatorio")
    Long userId
) {}