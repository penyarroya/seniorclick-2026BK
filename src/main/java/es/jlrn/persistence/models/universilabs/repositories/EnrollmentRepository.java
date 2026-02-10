package es.jlrn.persistence.models.universilabs.repositories;

import es.jlrn.persistence.models.universilabs.models.Enrollment;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
//    
    // Para evitar que un usuario se inscriba dos veces al mismo proyecto
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
    
    // Para ver todos los proyectos de un alumno
    //List<Enrollment> findByUserId(Long userId);
    
    // Para ver todos los alumnos de un proyecto
    List<Enrollment> findByProjectId(Long projectId);

    // Para buscar una inscripción específica
    Optional<Enrollment> findByUserIdAndProjectId(Long userId, Long projectId);

    // NUEVO: Para eliminar una inscripción específica directamente
    // El @Transactional es obligatorio para borrados personalizados
    @Transactional
    void deleteByUserIdAndProjectId(Long userId, Long projectId);

    @Query("SELECT e FROM Enrollment e " +
       "LEFT JOIN FETCH e.project " +
       "WHERE e.user.id = :userId")
    List<Enrollment> findByUserId(Long userId);

    // Agregamos un método para obtener el ID de la última página consultando la tabla UserProgress
    @Query("SELECT up.page.id FROM UserProgress up " +
        "WHERE up.enrollment.id = :enrollmentId " +
        "ORDER BY up.lastAccess DESC LIMIT 1")
    Long findLastVisitedPageId(Long enrollmentId);
}