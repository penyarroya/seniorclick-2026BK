package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Contribution;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionResponseDTO;

public interface IContributionMapper {
//    
    ContributionResponseDTO toResponseDTO(Contribution entity);
    Contribution toEntity(ContributionRequestDTO dto);
}