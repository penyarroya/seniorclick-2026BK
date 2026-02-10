package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Institution;
import es.jlrn.presentation.universilabs.dtos.institutions.InstitutionDTO;

public interface InstitutionMapper {
    InstitutionDTO toDTO(Institution entity);
    Institution toEntity(InstitutionDTO dto);
}