package es.jlrn.persistence.models.universilabs.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.jlrn.persistence.models.universilabs.models.Subtopic;

public interface SubtopicRepository extends JpaRepository<Subtopic, Long> {
//    
    List<Subtopic> findByTopicId(Long topicId);
    Optional<Subtopic> findByTitleAndTopicId(String title, Long topicId);
}