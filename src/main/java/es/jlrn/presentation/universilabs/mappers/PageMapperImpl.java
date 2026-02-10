package es.jlrn.presentation.universilabs.mappers;

import org.springframework.stereotype.Component;
import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.presentation.universilabs.dtos.pages.PageRequestDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IPageMapper;

@Component
public class PageMapperImpl implements IPageMapper {

    @Override 
    public PageResponseDTO toResponseDTO(Page entity) {
        if (entity == null) return null;
        
        Long projectId = null;
        try {
            if (entity.getSubtopic() != null && 
                entity.getSubtopic().getTopic() != null && 
                entity.getSubtopic().getTopic().getCollection() != null &&
                entity.getSubtopic().getTopic().getCollection().getProject() != null) {
                
                projectId = entity.getSubtopic().getTopic().getCollection().getProject().getId();
            }
        } catch (NullPointerException e) {
            // Protección adicional
        }

        // Importante: Mantener el orden de los argumentos según tu Record PageResponseDTO
        return new PageResponseDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getFormat(), // <--- NUEVO: Mapeamos el enum a la respuesta
            entity.getContent(),
            entity.getSubtopic() != null ? entity.getSubtopic().getId() : null,
            entity.getSubtopic() != null ? entity.getSubtopic().getTitle() : "Sin Subtema",
            projectId,
            entity.getAuthor() != null ? entity.getAuthor().getId() : null,
            entity.getAuthor() != null ? entity.getAuthor().getUsername() : "Anónimo",
            entity.getCreatedAt(), 
            entity.getUpdatedAt()  
        );
    }

    @Override
    public Page toEntity(PageRequestDTO dto) {
        if (dto == null) return null;
        
        return Page.builder()
            .title(dto.title())
            .format(dto.format()) // <--- NUEVO: Mapeamos el enum al crear la entidad
            .content(dto.content())
            .build();
    }
}

// @Component
// public class PageMapperImpl implements IPageMapper {

//     @Override 
//     public PageResponseDTO toResponseDTO(Page entity) {
//         if (entity == null) return null;
        
//         // Obtenemos el ID del proyecto navegando por la jerarquía:
//         // Page -> Subtopic -> Topic -> Collection -> Project
//         Long projectId = null;
//         try {
//             if (entity.getSubtopic() != null && 
//                 entity.getSubtopic().getTopic() != null && 
//                 entity.getSubtopic().getTopic().getCollection() != null &&
//                 entity.getSubtopic().getTopic().getCollection().getProject() != null) {
                
//                 projectId = entity.getSubtopic().getTopic().getCollection().getProject().getId();
//             }
//         } catch (NullPointerException e) {
//             // Protección adicional ante datos huérfanos
//         }

//         return new PageResponseDTO(
//             entity.getId(),
//             entity.getTitle(),
//             entity.getContent(),
//             entity.getSubtopic() != null ? entity.getSubtopic().getId() : null,
//             entity.getSubtopic() != null ? entity.getSubtopic().getTitle() : "Sin Subtema",
//             projectId, // Crítico para que Angular sepa a qué proyecto pertenece la página
//             entity.getAuthor() != null ? entity.getAuthor().getId() : null,
//             entity.getAuthor() != null ? entity.getAuthor().getUsername() : "Anónimo",
//             entity.getCreatedAt(), 
//             entity.getUpdatedAt()  
//         );
//     }

//     @Override
//     public Page toEntity(PageRequestDTO dto) {
//         if (dto == null) return null;
        
//         return Page.builder()
//             .title(dto.title())
//             .content(dto.content())
//             .build();
//     }
// }