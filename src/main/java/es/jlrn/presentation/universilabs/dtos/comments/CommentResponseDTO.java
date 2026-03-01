package es.jlrn.presentation.universilabs.dtos.comments;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CommentResponseDTO(
    
    Long id,
    String content,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt,
    boolean resolved,      // Añadido: Para saber si la duda fue resuelta
    Long pageId,
    Long userId,
    String username,
    List<CommentResponseDTO> replies  // IMPORTANTE: Esta lista permite que el JSON sea un árbol de mensajes
) {}