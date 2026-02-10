package es.jlrn.presentation.universilabs.dtos.usersprogress;

import java.time.LocalDateTime;

public record UserProgressResponseDTO(
    Long id,
    String status,
    Integer timeSpentSeconds,
    LocalDateTime lastAccess,
    Long userId,
    Long pageId,
    // Nuevos campos para el flujo de usuario
    String motivationMessage,
    Long nextPageId,
    boolean isProjectFinished
) {}