package es.jlrn.presentation.universilabs.services.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.jlrn.presentation.universilabs.dtos.topics.TopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.topics.TopicResponseDTO;
import java.util.List;


public interface ITopicService {
//    
    List<TopicResponseDTO> findByCollectionId(Long collectionId);
    TopicResponseDTO findById(Long id);
    TopicResponseDTO save(TopicRequestDTO dto);
    TopicResponseDTO update(Long id, TopicRequestDTO dto);
    void delete(Long id);
    Page<TopicResponseDTO> findAll(Pageable pageable); // Añade esto
}