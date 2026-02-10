package es.jlrn.presentation.universilabs.dtos.comments;

import java.time.LocalDateTime;

public record CommentResponseDTO(
    
    Long id,
    String content,
    LocalDateTime createdAt,
    Long pageId,
    Long userId,
    String username
) {}