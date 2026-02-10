// package es.jlrn.persistence.models.universilabs.repositories;

// import java.util.List;

// //import org.springframework.data.domain.Page;
// //import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;

// import es.jlrn.persistence.models.universilabs.models.Page;
// //import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;

// public interface PageRepository extends JpaRepository<Page, Long> {
// //    
//     @Override
//     org.springframework.data.domain.Page<Page> findAll(org.springframework.data.domain.Pageable pageable);

//     List<Page> findBySubtopicId(Long subtopicId);
//     List<Page> findByAuthorId(Long authorId);

//     /**
//      * Esta consulta navega por toda la jerarquía de SeniorClick:
//      * Project -> Collection -> Topic -> Subtopic -> Page
//      * y las ordena para que el abuelo las vea en el orden correcto.
//      */
//     @Query("SELECT p FROM Page p " +
//            "JOIN p.subtopic s " +
//            "JOIN s.topic t " +
//            "JOIN t.collection c " +
//            "WHERE c.project.id = :projectId " +
//            "ORDER BY c.id ASC, t.id ASC, s.id ASC, p.id ASC")
//     List<Page> findAllPagesByProjectOrdered(@Param("projectId") Long projectId);

//     // Contar páginas en un proyecto específico
//     @Query("SELECT COUNT(p) FROM Page p " +
//         "JOIN p.subtopic s JOIN s.topic t JOIN t.collection c " +
//         "WHERE c.project.id = :projectId")
//     long countPagesByProjectId(@Param("projectId") Long projectId);
// }

package es.jlrn.persistence.models.universilabs.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import es.jlrn.persistence.models.universilabs.models.Page;

public interface PageRepository extends JpaRepository<Page, Long> {

    @Override
    org.springframework.data.domain.Page<Page> findAll(org.springframework.data.domain.Pageable pageable);

    // 1. OPTIMIZACIÓN: Carga autor y subtema de golpe para evitar N+1 en el Mapper
    @EntityGraph(attributePaths = {"author", "subtopic"})
    Optional<Page> findById(Long id);

    @EntityGraph(attributePaths = {"author", "subtopic"})
    List<Page> findBySubtopicId(Long subtopicId);

    List<Page> findByAuthorId(Long authorId);

    /**
     * Navegación jerárquica: Project -> Collection -> Topic -> Subtopic -> Page
     */
    @Query("SELECT p FROM Page p " +
           "JOIN p.subtopic s " +
           "JOIN s.topic t " +
           "JOIN t.collection c " +
           "WHERE c.project.id = :projectId " +
           "ORDER BY c.id ASC, t.id ASC, s.id ASC, p.id ASC")
    List<Page> findAllPagesByProjectOrdered(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(p) FROM Page p " +
           "JOIN p.subtopic s JOIN s.topic t JOIN t.collection c " +
           "WHERE c.project.id = :projectId")
    long countPagesByProjectId(@Param("projectId") Long projectId);
}