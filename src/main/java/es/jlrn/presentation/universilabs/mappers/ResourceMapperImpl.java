package es.jlrn.presentation.universilabs.mappers;

import es.jlrn.persistence.models.universilabs.models.Resource;
import es.jlrn.presentation.universilabs.dtos.resources.ResourceDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IResourceMapper;
import es.jlrn.persistence.models.universilabs.models.Page;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapperImpl implements IResourceMapper {
//
    @Override
    public ResourceDTO toDto(Resource entity) {
        if (entity == null) return null;

        return ResourceDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .url(entity.getUrl())
                .order(entity.getOrder())
                .pageId(entity.getPage() != null ? entity.getPage().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Resource toEntity(ResourceDTO dto, Page page) {
        if (dto == null) return null;

        return Resource.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .type(dto.getType())
                .url(dto.getUrl())
                .order(dto.getOrder())
                .page(page) // Asignamos el objeto Page completo
                .build();
    }
}