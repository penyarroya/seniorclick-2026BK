package es.jlrn.persistence.models.universilabs.repositories;

import es.jlrn.persistence.models.universilabs.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
//    
    List<Topic> findByCollectionId(Long collectionId);
    Optional<Topic> findByTitleAndCollectionId(String title, Long collectionId);
}