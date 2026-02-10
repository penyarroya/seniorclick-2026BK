package es.jlrn.presentation.universilabs.dtos.pages;

import java.util.List;

import es.jlrn.persistence.enums.PageFormat;
import es.jlrn.presentation.universilabs.dtos.resources.ResourceDTO;
import lombok.Builder;

/**
 * Record que representa el detalle completo de una página para el usuario.
 * Incluye información de progreso y navegación.
 */
@Builder
public record PageDetailDTO(
    Long id,
    String title,
    PageFormat format, // <--- Nuevo
    String content,
    List<ResourceDTO> resources,
    String userStatus,
    Integer timeSpentSeconds,
    boolean isLastPage
) {}
// @Builder
// public record PageDetailDTO(
//     Long id,
//     String title,
//     String content,
//     List<ResourceDTO> resources,
//     String userStatus,      // COMPLETED, IN_PROGRESS, NOT_STARTED
//     Integer timeSpentSeconds,
//     boolean isLastPage      // Indica si es la última lección del proyecto
// ) {}