package es.jlrn.presentation.universilabs.dtos.enrollments;

import jakarta.validation.constraints.*;

public record EnrollmentRequestDTO(
//    
    @NotNull(message = "El ID de usuario es obligatorio")
    Long userId,
    
    @NotNull(message = "El ID del proyecto es obligatorio")
    Long projectId,
    
    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "LEARNER|MENTOR|COLLABORATOR", message = "Rol no válido")
    String roleInCourse
) {}