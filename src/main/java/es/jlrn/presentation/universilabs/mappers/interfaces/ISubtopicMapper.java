package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Subtopic;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicResponseDTO;

public interface ISubtopicMapper {
//    
    SubtopicResponseDTO toResponseDTO(Subtopic entity);
    
    Subtopic toEntity(SubtopicRequestDTO dto);
}