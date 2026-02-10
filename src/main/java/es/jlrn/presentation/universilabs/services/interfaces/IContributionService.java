package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;

import es.jlrn.presentation.universilabs.dtos.contributions.ContributionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionResponseDTO;

public interface IContributionService {
//    
    List<ContributionResponseDTO> getByPage(Long pageId);
    ContributionResponseDTO save(ContributionRequestDTO dto);
    void delete(Long id);
    List<ContributionResponseDTO> list();
}