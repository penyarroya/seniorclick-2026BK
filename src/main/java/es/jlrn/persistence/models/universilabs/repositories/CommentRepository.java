package es.jlrn.persistence.models.universilabs.repositories;

import es.jlrn.persistence.models.universilabs.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
//
    /**
     * Busca todos los comentarios de una página específica
     * ordenados por fecha de creación (los más recientes primero).
     */
    List<Comment> findByPageIdOrderByCreatedAtDesc(Long pageId);

    /**
     * Opcional: Busca comentarios de un usuario específico
     */
    List<Comment> findByUserId(Long userId);

    // Para la sección "Mis Aportaciones" (Comunidad)
    // Cambiamos el original por uno ordenado:
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Para la lista global de moderación o el feed de comunidad
    List<Comment> findAllByOrderByCreatedAtDesc();
    
    // Útil para mostrar un badge de "X comentarios" en la lista de páginas
    long countByPageId(Long pageId);

    /**
     * IMPORTANTE: Obtiene solo los comentarios raíz (preguntas originales) 
     * de una página. Las respuestas vendrán dentro de la lista 'replies' de cada objeto 
     * gracias al FetchType de la entidad.
     */
    List<Comment> findByPageIdAndParentIsNullOrderByCreatedAtDesc(Long pageId);

    /**
     * Busca comentarios pendientes de resolver. 
     * Ideal para el panel de administración/soporte de Ayuda y FAQ.
     */
    List<Comment> findByResolvedFalseOrderByCreatedAtAsc();

    /**
     * Cuenta cuántas preguntas (comentarios padre) hay en una página.
     */
    long countByPageIdAndParentIsNull(Long pageId);

    /**
     * Búsqueda por contenido para el buscador de la sección de Ayuda.
     */
    List<Comment> findByContentContainingIgnoreCase(String keyword);

    // Si necesitas todos los comentarios de un hilo específico
    List<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId);
    
    // Para evitar duplicados: verifica si ya existe un comentario con el mismo contenido en la misma página
    boolean existsByContentIgnoreCaseAndPageId(String content, Long pageId);
}