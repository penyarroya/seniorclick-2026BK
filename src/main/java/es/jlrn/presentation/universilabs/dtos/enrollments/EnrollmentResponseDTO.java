package es.jlrn.presentation.universilabs.dtos.enrollments;

import java.time.LocalDateTime;

public record EnrollmentResponseDTO(
//    
    Long id,
    Long userId,
    String userName,
    Long projectId,
    String projectTitle,
    String roleInCourse,
    LocalDateTime createdAt,
    Integer lastPageVisited,      // <--- Añadido
    Integer progressPercentage    // <--- Añadido (para la barra de progreso)

) {}