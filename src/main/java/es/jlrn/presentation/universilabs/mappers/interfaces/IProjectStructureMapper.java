package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Project;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectRequestDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectResponseDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectStructureDTO;

public interface IProjectStructureMapper {
//
    // Para obtener la respuesta básica del proyecto
    ProjectResponseDTO toResponseDTO(Project entity);

    // Para convertir el DTO de creación en entidad
    Project toEntity(ProjectRequestDTO dto);

    // EL MÉTODO CLAVE: Para obtener todo el árbol jerárquico (menú lateral)
    ProjectStructureDTO toStructureDTO(Project entity);

}
