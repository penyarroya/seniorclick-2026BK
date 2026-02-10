package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.presentation.universilabs.dtos.pages.PageRequestDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;

public interface IPageMapper {
//
    PageResponseDTO toResponseDTO(Page entity);

    Page toEntity(PageRequestDTO dto);
}