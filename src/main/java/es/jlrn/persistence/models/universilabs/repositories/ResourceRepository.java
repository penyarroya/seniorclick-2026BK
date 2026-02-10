package es.jlrn.persistence.models.universilabs.repositories;

import es.jlrn.persistence.models.universilabs.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
//    
    // Para obtener todos los recursos de una página ordenados (útil para el frontend)
    List<Resource> findByPageIdOrderByOrderAsc(Long pageId);
}