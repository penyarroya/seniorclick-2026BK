// package es.jlrn.presentation.universilabs.mappers;

// import es.jlrn.persistence.models.universilabs.models.*;
// import es.jlrn.presentation.universilabs.dtos.projects.*;
// import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicNodeDTO;
// import es.jlrn.presentation.universilabs.dtos.pages.PageSummaryNodeDTO;
// import es.jlrn.presentation.universilabs.mappers.interfaces.IProjectStructureMapper;

// import org.springframework.stereotype.Component;
// import java.util.List;
// import java.util.stream.Collectors;

// @Component
// public class ProjectStructureMapperImpl implements IProjectStructureMapper {

//     @Override
//     public ProjectResponseDTO toResponseDTO(Project entity) {
//         if (entity == null) return null;
//         return ProjectResponseDTO.builder()
//                 .id(entity.getId())
//                 .title(entity.getTitle())
//                 .activo(entity.isActivo())
//                 .build();
//     }

//     @Override
//     public Project toEntity(ProjectRequestDTO dto) {
//         if (dto == null) return null;
//         return Project.builder()
//                 .title(dto.getTitle())
//                 .activo(dto.getActivo())
//                 .build();
//     }

//     // NIVEL 1: PROYECTO (Carrera)
//     @Override
//     public ProjectStructureDTO toStructureDTO(Project entity) {
//         if (entity == null) return null;

//         return ProjectStructureDTO.builder()
//                 .id(entity.getId())
//                 .title(entity.getTitle())
//                 .description(entity.getDescription())
//                 .institutionName(entity.getInstitution() != null ? entity.getInstitution().getName() : null)
//                 .modules(entity.getCollections() != null 
//                     ? entity.getCollections().stream()
//                         .map(this::mapToModuleStructureDTO) 
//                         .collect(Collectors.toList()) 
//                     : List.of())
//                 .build();
//     }

//     // NIVEL 2: COLECCIÓN -> MÓDULO (Bloque/Cuatrimestre)
//     private ModuleStructureDTO mapToModuleStructureDTO(CollectionEntity entity) {
//         if (entity == null) return null;
        
//         return ModuleStructureDTO.builder()
//                 .id(entity.getId())
//                 .title(entity.getName())
//                 .pages(entity.getTopics() != null 
//                     ? entity.getTopics().stream()
//                         .map(this::mapTopicToPageStructureDTO) 
//                         .collect(Collectors.toList()) 
//                     : List.of())
//                 .build();
//     }

//     // NIVEL 3: TEMA -> ASIGNATURA (Aquí conectamos con los métodos de abajo para quitar el warning)
//     private PageStructureDTO mapTopicToPageStructureDTO(Topic entity) {
//         if (entity == null) return null;
        
//         return PageStructureDTO.builder()
//                 .id(entity.getId())
//                 .title(entity.getTitle())
//                 // Si PageStructureDTO tiene un campo para subtemas, lo mapeamos así:
//                 .subtopics(entity.getSubtopics() != null 
//                     ? entity.getSubtopics().stream().map(this::mapSubtopic).collect(Collectors.toList()) 
//                     : List.of())
//                 .build();
//     }

//     // NIVEL 4: SUBTEMA -> UNIDAD DIDÁCTICA
//     private SubtopicNodeDTO mapSubtopic(Subtopic entity) {
//         if (entity == null) return null;
//         return SubtopicNodeDTO.builder()
//                 .id(entity.getId())
//                 .title(entity.getTitle())
//                 .pages(entity.getPages() != null 
//                     ? entity.getPages().stream().map(this::mapPageSummary).collect(Collectors.toList()) 
//                     : List.of())
//                 .build();
//     }

//     // NIVEL 5: PÁGINA -> LECCIÓN
//     // private PageSummaryNodeDTO mapPageSummary(Page entity) {
//     //     if (entity == null) return null;
//     //     return PageSummaryNodeDTO.builder()
//     //             .id(entity.getId())
//     //             .title(entity.getTitle())
//     //             .build();
//     // }

//     // NIVEL 5: PÁGINA -> LECCIÓN (Con Recursos)
//     private PageSummaryNodeDTO mapPageSummary(Page entity) {
//         if (entity == null) return null;

//         return PageSummaryNodeDTO.builder()
//                 .id(entity.getId())
//                 .title(entity.getTitle())
//                 // Mapeamos la lista de recursos de la entidad Page
//                 .resources(entity.getResources() != null 
//                     ? entity.getResources().stream()
//                         .map(this::mapResourceToDTO)
//                         .collect(Collectors.toList()) 
//                     : List.of())
//                 .build();
//     }

//     // Método auxiliar para mapear el recurso individual
//     private ProjectResourceDTO mapResourceToDTO(Resource res) {
//         return ProjectResourceDTO.builder()
//                 .name(res.getTitle()) // Usamos 'title' de tu entidad Resource
//                 .url(res.getUrl())
//                 .type(res.getType() != null ? res.getType().name().toLowerCase() : "link")
//                 .build();
//     }
// }

package es.jlrn.presentation.universilabs.mappers;

import es.jlrn.persistence.models.universilabs.models.*;
import es.jlrn.presentation.universilabs.dtos.projects.*;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicNodeDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageSummaryNodeDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IProjectStructureMapper;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectStructureMapperImpl implements IProjectStructureMapper {

    @Override
    public ProjectResponseDTO toResponseDTO(Project entity) {
        if (entity == null) return null;
        return ProjectResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .activo(entity.isActivo())
                .build();
    }

    @Override
    public Project toEntity(ProjectRequestDTO dto) {
        if (dto == null) return null;
        return Project.builder()
                .title(dto.getTitle())
                .activo(dto.getActivo())
                .build();
    }

    // NIVEL 1: PROYECTO (Estructura Raíz)
    @Override
    public ProjectStructureDTO toStructureDTO(Project entity) {
        if (entity == null) return null;

        return ProjectStructureDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .institutionName(entity.getInstitution() != null ? entity.getInstitution().getName() : null)
                .modules(entity.getCollections() != null 
                    ? entity.getCollections().stream()
                        .map(this::mapToModuleStructureDTO) 
                        .collect(Collectors.toList()) 
                    : List.of())
                .build();
    }

    // NIVEL 2: COLECCIÓN -> MÓDULO
    private ModuleStructureDTO mapToModuleStructureDTO(CollectionEntity entity) {
        if (entity == null) return null;
        
        return ModuleStructureDTO.builder()
                .id(entity.getId())
                .title(entity.getName())
                .pages(entity.getTopics() != null 
                    ? entity.getTopics().stream()
                        .map(this::mapTopicToPageStructureDTO) 
                        .collect(Collectors.toList()) 
                    : List.of())
                .build();
    }

    // NIVEL 3: TEMA
    private PageStructureDTO mapTopicToPageStructureDTO(Topic entity) {
        if (entity == null) return null;
        
        return PageStructureDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .subtopics(entity.getSubtopics() != null 
                    ? entity.getSubtopics().stream().map(this::mapSubtopic).collect(Collectors.toList()) 
                    : List.of())
                .build();
    }

    // NIVEL 4: SUBTEMA
    private SubtopicNodeDTO mapSubtopic(Subtopic entity) {
        if (entity == null) return null;
        return SubtopicNodeDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pages(entity.getPages() != null 
                    ? entity.getPages().stream().map(this::mapPageSummary).collect(Collectors.toList()) 
                    : List.of())
                .build();
    }

    // NIVEL 5: PÁGINA (Con mapeo dinámico de recursos)
    private PageSummaryNodeDTO mapPageSummary(Page entity) {
        if (entity == null) return null;

        return PageSummaryNodeDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .resources(entity.getResources() != null 
                    ? entity.getResources().stream()
                        .map(this::mapResourceToDTO)
                        .collect(Collectors.toList()) 
                    : List.of())
                .build();
    }

    // Método auxiliar para transformar la Entidad Resource al DTO que espera Angular
    private ProjectResourceDTO mapResourceToDTO(Resource res) {
        if (res == null) return null;
        return ProjectResourceDTO.builder()
                .name(res.getTitle())
                .url(res.getUrl())
                .type(res.getType() != null ? res.getType().name().toLowerCase() : "link")
                .build();
    }
}