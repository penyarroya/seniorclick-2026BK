package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.persistence.models.universilabs.models.Resource;
import es.jlrn.presentation.universilabs.dtos.resources.ResourceDTO;

public interface IResourceMapper {
//
    ResourceDTO toDto(Resource entity);
    Resource toEntity(ResourceDTO dto, Page page);
}
