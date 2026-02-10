package es.jlrn.persistence.models.universilabs.repositories;

import es.jlrn.persistence.models.universilabs.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
//
    // --- MANTENIMIENTO DE INTEGRIDAD (Ignoran el flag 'activo') ---
    
    // Necesario para el 'save' y 'update' para evitar errores de restricción UNIQUE en la DB
    Optional<Project> findByTitleAndInstitutionId(String title, Long institutionId);


    // --- CONSULTAS PÚBLICAS (Solo proyectos activos) ---

    // Lista principal de proyectos visibles
    Page<Project> findByActivoTrue(Pageable pageable);

    // Detalle de un proyecto específico (evita que se acceda a uno borrado lógicamente)
    Optional<Project> findByIdAndActivoTrue(Long id);

    // Búsqueda filtrada: Usamos @Query para que el nombre del método sea humano y legible
    @Query("SELECT p FROM Project p WHERE p.activo = true AND LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Project> searchActiveProjects(@Param("searchTerm") String searchTerm, Pageable pageable);


    // --- CONSULTAS DE SOPORTE / ADMIN (Opcionales) ---

    // Si alguna vez necesitas ver proyectos borrados por título
    Page<Project> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    // En ProjectRepository.java
    List<Project> findByActivoTrue();
}