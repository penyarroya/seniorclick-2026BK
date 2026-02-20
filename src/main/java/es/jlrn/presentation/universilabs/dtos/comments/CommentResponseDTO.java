package es.jlrn.presentation.universilabs.dtos.comments;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponseDTO(
    
    Long id,
    String content,
    LocalDateTime createdAt,
    boolean resolved,      // Añadido: Para saber si la duda fue resuelta
    Long pageId,
    Long userId,
    String username,
    // IMPORTANTE: Esta lista permite que el JSON sea un árbol de mensajes
    List<CommentResponseDTO> replies
) {}