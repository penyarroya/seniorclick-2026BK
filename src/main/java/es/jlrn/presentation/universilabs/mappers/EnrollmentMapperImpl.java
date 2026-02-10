package es.jlrn.presentation.universilabs.mappers;

import es.jlrn.persistence.models.universilabs.models.Enrollment;
import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IEnrollmentMapper;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapperImpl implements IEnrollmentMapper {

    @Override
    public EnrollmentResponseDTO toResponseDTO(Enrollment entity) {
        if (entity == null) return null;

        Long userId = (entity.getUser() != null) ? entity.getUser().getId() : null;
        String userName = (entity.getUser() != null) ? entity.getUser().getUsername() : "N/A";
        
        Long projectId = (entity.getProject() != null) ? entity.getProject().getId() : null;
        String projectTitle = (entity.getProject() != null) ? entity.getProject().getTitle() : "Proyecto no encontrado";

        // Ahora pasamos los 9 argumentos requeridos por el Record
        return new EnrollmentResponseDTO(
            entity.getId(),
            userId,
            userName,
            projectId,
            projectTitle,
            entity.getRoleInCourse(),
            entity.getCreatedAt(),
            1, // lastPageVisited (valor por defecto, el Service lo cambiará)
            entity.getProgressPercentage() != null ? entity.getProgressPercentage() : 0 // progressPercentage
        );
    }
}


// @Component
// public class EnrollmentMapperImpl implements IEnrollmentMapper {
// //
//     @Override
//     public EnrollmentResponseDTO toResponseDTO(Enrollment entity) {
//         if (entity == null) return null;

//         Long userId = (entity.getUser() != null) ? entity.getUser().getId() : null;
//         String userName = (entity.getUser() != null) ? entity.getUser().getUsername() : "N/A";
        
//         Long projectId = (entity.getProject() != null) ? entity.getProject().getId() : null;
//         String projectTitle = (entity.getProject() != null) ? entity.getProject().getTitle() : "Proyecto no encontrado";

//         return new EnrollmentResponseDTO(
//             entity.getId(),
//             userId,
//             userName,
//             projectId,
//             projectTitle,
//             entity.getRoleInCourse(),
//             entity.getCreatedAt()
//         );
//     }
// }