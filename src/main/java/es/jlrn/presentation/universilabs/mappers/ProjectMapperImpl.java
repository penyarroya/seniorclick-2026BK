package es.jlrn.presentation.universilabs.mappers;

import es.jlrn.persistence.models.universilabs.models.Project;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectRequestDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IProjectMapper;

import org.springframework.stereotype.Component;

@Component
public class ProjectMapperImpl implements IProjectMapper {

    @Override
    public ProjectResponseDTO toResponseDTO(Project entity) {
        if (entity == null) return null;

        ProjectResponseDTO dto = ProjectResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .level(entity.getLevel())
                .activo(entity.isActivo()) // <--- 1. De la Entidad al DTO (para Angular)
                .build();

        // Mapeo seguro de Institución
        if (entity.getInstitution() != null) {
            dto.setInstitutionId(entity.getInstitution().getId());
            dto.setInstitutionName(entity.getInstitution().getName());
        }

        // Mapeo seguro de Usuario (Creador)
        if (entity.getCreatedBy() != null) {
            dto.setCreatorName(entity.getCreatedBy().getUsername());
        }

        return dto;
    }

    @Override
    public Project toEntity(ProjectRequestDTO dto) {
        if (dto == null) return null;

        return Project.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .level(dto.getLevel())
                .activo(dto.getActivo()) // <--- 2. Del DTO a la Entidad (para la BD)
                .build();
    }
}