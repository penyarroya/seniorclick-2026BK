// package es.jlrn.persistence.models.universilabs.repositories;

// import java.util.List;
// import java.util.Optional;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import es.jlrn.persistence.models.universilabs.models.Enrollment;
// import es.jlrn.persistence.models.universilabs.models.UserProgress;

// public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {

//     // 1. Buscar por IDs largos (Útil para controladores)
//     Optional<UserProgress> findByEnrollment_User_IdAndPage_Id(Long userId, Long pageId);

//     // 2. Obtener lista de progreso por ID de usuario
//     List<UserProgress> findByEnrollment_User_Id(Long userId);
    
//     // 3. Buscar usando el objeto Enrollment (El que usamos en el Service)
//     // Eliminamos el que decía "Object" y dejamos el de "Enrollment"
//     Optional<UserProgress> findByEnrollmentAndPage_Id(Enrollment enrollment, Long pageId);
//     // En UserProgressRepository.java
//     Optional<UserProgress> findFirstByEnrollmentOrderByLastAccessDesc(Enrollment enrollment);

//     //4. Contar páginas completadas por usuario en un proyecto específico
//     @Query("SELECT COUNT(up) FROM UserProgress up " +
//         "WHERE up.enrollment.user.id = :userId " +
//         "AND up.enrollment.project.id = :projectId " +
//         "AND up.status = 'COMPLETED'")
//     long countCompletedPagesByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);
//     }

package es.jlrn.persistence.models.universilabs.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.jlrn.persistence.models.universilabs.models.Enrollment;
import es.jlrn.persistence.models.universilabs.models.UserProgress;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
//
    // 1. Buscar por IDs (Útil para controladores)
    Optional<UserProgress> findByEnrollment_User_IdAndPage_Id(Long userId, Long pageId);
    // UserProgressRepository.java
    @Query("SELECT up FROM UserProgress up JOIN up.enrollment e WHERE e.user.id = :userId")
    List<UserProgress> findByUserId(@Param("userId") Long userId);

    // 2. Obtener lista de progreso por ID de usuario con carga optimizada (JOIN FETCH)
    // Se ha movido aquí para que sea la única definición de este método
    @Query("SELECT up FROM UserProgress up " +
           "JOIN FETCH up.enrollment e " +
           "JOIN FETCH e.user u " +
           "JOIN FETCH up.page p " +
           "WHERE u.id = :userId")
    List<UserProgress> findByEnrollment_User_Id(@Param("userId") Long userId);
    
    // 3. Otros métodos de búsqueda
    Optional<UserProgress> findByEnrollmentAndPage_Id(Enrollment enrollment, Long pageId);
    
    Optional<UserProgress> findFirstByEnrollmentOrderByLastAccessDesc(Enrollment enrollment);

    // 4. Conteo de páginas (descomentado si lo necesitas)
    @Query("SELECT COUNT(up) FROM UserProgress up " +
           "WHERE up.enrollment.user.id = :userId " +
           "AND up.enrollment.project.id = :projectId " +
           "AND up.status = 'COMPLETED'")
    long countCompletedPagesByUserIdAndProjectId(@Param("userId") Long userId, @Param("projectId") Long projectId);

    Optional<UserProgress> findFirstByEnrollmentIdOrderByLastAccessDesc(Long enrollmentId);
}