package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Project;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectRequestDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectResponseDTO;

public interface IProjectMapper {
//
    ProjectResponseDTO toResponseDTO(Project entity);
    Project toEntity(ProjectRequestDTO dto);

    //ProjectStructureDTO toStructureDTO(Project entity);
}
