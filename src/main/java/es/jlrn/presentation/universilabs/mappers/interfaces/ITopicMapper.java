package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Topic;
import es.jlrn.presentation.universilabs.dtos.topics.TopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.topics.TopicResponseDTO;

public interface ITopicMapper {
//    
    TopicResponseDTO toResponseDTO(Topic entity);
    Topic toEntity(TopicRequestDTO dto);
}
