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

    // AÑADE ESTE: Para la lista global de moderación
    List<Comment> findAllByOrderByCreatedAtDesc();
}