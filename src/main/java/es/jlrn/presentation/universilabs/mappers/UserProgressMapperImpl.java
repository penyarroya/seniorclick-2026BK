// package es.jlrn.presentation.universilabs.mappers;

// import java.time.LocalDateTime;
// import org.springframework.stereotype.Component;

// import es.jlrn.persistence.models.universilabs.models.UserProgress;
// import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;
// import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressResponseDTO;
// import es.jlrn.presentation.universilabs.mappers.interfaces.IPageMapper;
// import es.jlrn.presentation.universilabs.mappers.interfaces.IUserProgressMapper;

// @Component
// public class UserProgressMapperImpl implements IUserProgressMapper {
// //
//     @Component
// public class PageMapperImpl implements IPageMapper {

//     @Override
//     public PageResponseDTO toResponseDTO(Page entity) {
//         if (entity == null) return null;

//             return new PageResponseDTO(
//                 entity.getId(),
//                 entity.getTitle(),
//                 entity.getContent(),
//                 entity.getSubtopic().getId(),
//                 entity.getSubtopic().getTitle(),
//                 // --- ESTE ES EL CAMBIO CLAVE ---
//                 entity.getSubtopic().getTopic().getCollection().getProject().getId(),
//                 // -------------------------------
//                 entity.getAuthor().getId(),
//                 entity.getAuthor().getUsername(),
//                 entity.getCreatedAt(),
//                 entity.getUpdatedAt()
//             );
//         }
//     }

//     @Override
//     public UserProgress toEntity(UserProgressRequestDTO dto) {
//         if (dto == null) return null;
//         return UserProgress.builder()
//             .status(dto.status())
//             .timeSpentSeconds(dto.timeSpentSeconds() != null ? dto.timeSpentSeconds() : 0)
//             .lastAccess(LocalDateTime.now())
//             .build();
//     }
// }

package es.jlrn.presentation.universilabs.mappers;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import es.jlrn.persistence.models.universilabs.models.UserProgress;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressRequestDTO;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IUserProgressMapper;

@Component
public class UserProgressMapperImpl implements IUserProgressMapper {

    @Override
    public UserProgressResponseDTO toResponseDTO(UserProgress entity) {
        if (entity == null) return null;
        
        return new UserProgressResponseDTO(
            entity.getId(),
            entity.getStatus(),
            entity.getTimeSpentSeconds(),
            entity.getLastAccess(),
            entity.getEnrollment().getUser().getId(), 
            entity.getPage().getId(),
            // Estos campos son los que el Service rellenará en la lógica de completar página
            null,  // motivationMessage
            null,  // nextPageId
            false  // isProjectFinished
        );
    }

    @Override
    public UserProgress toEntity(UserProgressRequestDTO dto) {
        if (dto == null) return null;
        return UserProgress.builder()
            .status(dto.status())
            .timeSpentSeconds(dto.timeSpentSeconds() != null ? dto.timeSpentSeconds() : 0)
            .lastAccess(LocalDateTime.now())
            .build();
    }
}