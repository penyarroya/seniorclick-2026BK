package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Enrollment;
import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentResponseDTO;

public interface IEnrollmentMapper {
//    
    /**
     * Convierte la entidad Enrollment a un DTO de respuesta.
     * Incluye datos del usuario y del proyecto para facilitar el trabajo al frontend.
     */
    EnrollmentResponseDTO toResponseDTO(Enrollment entity);
}