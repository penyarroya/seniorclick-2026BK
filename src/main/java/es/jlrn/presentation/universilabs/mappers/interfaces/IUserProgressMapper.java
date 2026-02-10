package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.UserProgress;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressRequestDTO;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressResponseDTO;

public interface IUserProgressMapper {
//    
    UserProgressResponseDTO toResponseDTO(UserProgress entity);
    
    UserProgress toEntity(UserProgressRequestDTO dto);
}