package es.jlrn.presentation.universilabs.mappers;

import es.jlrn.persistence.models.universilabs.models.Topic;
import es.jlrn.presentation.universilabs.dtos.topics.TopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.topics.TopicResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ITopicMapper;
import org.springframework.stereotype.Component;

@Component
public class TopicMapperImpl implements ITopicMapper {
//
    @Override
    public TopicResponseDTO toResponseDTO(Topic entity) {
        if (entity == null) {
            return null;
        }

        return new TopicResponseDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getCollection() != null ? entity.getCollection().getId() : null
        );
    }

    @Override
    public Topic toEntity(TopicRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        // Nota: El objeto CollectionEntity se asigna en el Service 
        // para asegurar que viene de la base de datos.
        return Topic.builder()
            .title(dto.title())
            .build();
    }
}