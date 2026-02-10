package es.jlrn.persistence.models.universilabs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.jlrn.persistence.models.universilabs.models.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionEntity, Long> {
//    
    // Para listar todas las colecciones de un proyecto específico
    List<CollectionEntity> findByProjectId(Long projectId);

    // Para validar la restricción única (Nombre + Proyecto)
    Optional<CollectionEntity> findByNameAndProjectId(String name, Long projectId);
    Page<CollectionEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}