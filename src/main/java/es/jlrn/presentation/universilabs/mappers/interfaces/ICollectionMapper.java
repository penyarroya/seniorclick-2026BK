package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.CollectionEntity;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionResponseDTO;

public interface ICollectionMapper {
//
    CollectionEntity toEntity(CollectionRequestDTO dto);
    CollectionResponseDTO toResponseDTO(CollectionEntity entity);
}
