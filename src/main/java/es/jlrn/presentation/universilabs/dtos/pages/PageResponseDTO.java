package es.jlrn.presentation.universilabs.dtos.pages;

import java.time.LocalDateTime;

import es.jlrn.persistence.enums.PageFormat;

public record PageResponseDTO(
    Long id,
    String title,
    PageFormat format, // <--- Nuevo
    String content,
    Long subtopicId,
    String subtopicTitle,
    Long projectId,
    Long authorId,
    String authorName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

// public record PageResponseDTO(
//     Long id,
//     String title,
//     String content,
//     Long subtopicId,
//     String subtopicTitle,
//     Long projectId,    // Posición 6: Coincide con el Mapper
//     Long authorId,     // Posición 7
//     String authorName, // Posición 8
//     LocalDateTime createdAt,
//     LocalDateTime updatedAt
// ) {}