package es.jlrn.presentation.universilabs.mappers;

import es.jlrn.persistence.models.universilabs.models.CollectionEntity;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ICollectionMapper;

import org.springframework.stereotype.Component;

@Component
public class CollectionMapperImpl implements ICollectionMapper {
//
    @Override
    public CollectionEntity toEntity(CollectionRequestDTO dto) {
        if (dto == null) return null;

        return CollectionEntity.builder()
                .name(dto.getName())
                // Nota: El proyecto se asigna en el Service mediante el ID
                .build();
    }

    @Override
    public CollectionResponseDTO toResponseDTO(CollectionEntity entity) {
        if (entity == null) return null;

        CollectionResponseDTO dto = new CollectionResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        if (entity.getProject() != null) {
            dto.setProjectId(entity.getProject().getId());
            dto.setProjectName(entity.getProject().getTitle());
        }

        // NUEVO: Conteo de temas para mostrar en el mantenimiento
        if (entity.getTopics() != null) {
            dto.setTopicsCount(entity.getTopics().size());
        } else {
            dto.setTopicsCount(0);
        }

        return dto;
    }
}