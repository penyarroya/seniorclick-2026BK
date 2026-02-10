package es.jlrn.presentation.universilabs.mappers;

import org.springframework.stereotype.Component;

import es.jlrn.persistence.models.universilabs.models.Subtopic;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ISubtopicMapper;

@Component
public class SubtopicMapperImpl implements ISubtopicMapper {
//    
    @Override
    public SubtopicResponseDTO toResponseDTO(Subtopic entity) {
        if (entity == null) return null;
        return new SubtopicResponseDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getTopic().getId()
        );
    }

    @Override
    public Subtopic toEntity(SubtopicRequestDTO dto) {
        if (dto == null) return null;
        return Subtopic.builder()
            .title(dto.title())
            .build();
    }
}