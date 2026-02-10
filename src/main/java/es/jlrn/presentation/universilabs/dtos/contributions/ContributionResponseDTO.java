package es.jlrn.presentation.universilabs.dtos.contributions;

import java.time.LocalDateTime;

public record ContributionResponseDTO(
//    
    Long id,
    String content,
    LocalDateTime createdAt,
    Long pageId,
    String pageTitle,
    Long userId,
    String username

) {}