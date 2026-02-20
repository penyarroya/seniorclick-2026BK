// package es.jlrn.presentation.universilabs.mappers;

// import org.springframework.stereotype.Component;

// import es.jlrn.persistence.models.universilabs.models.Comment;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
// import es.jlrn.presentation.universilabs.mappers.interfaces.ICommentMapper;

// @Component
// public class CommentMapperImpl implements ICommentMapper {
// //    
//     @Override
//     public CommentResponseDTO toResponseDTO(Comment entity) {
//         if (entity == null) return null;
        
//         return new CommentResponseDTO(
//             entity.getId(),
//             entity.getContent(),
//             entity.getCreatedAt(),
//             entity.getPage().getId(),
//             entity.getUser().getId(),
//             entity.getUser().getUsername() // Extraemos el username para el DTO
//         );
//     }

//     @Override
//     public Comment toEntity(CommentRequestDTO dto) {
//         if (dto == null) return null;
        
//         // Solo mapeamos el contenido; las relaciones (Page y User) 
//         // se asignan en el Service tras validarlas en la DB.
//         return Comment.builder()
//             .content(dto.content())
//             .build();
//     }
// }


package es.jlrn.presentation.universilabs.mappers;

import org.springframework.stereotype.Component;
import es.jlrn.persistence.models.universilabs.models.Comment;
import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ICommentMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapperImpl implements ICommentMapper {

    @Override
    public CommentResponseDTO toResponseDTO(Comment entity) {
        if (entity == null) return null;
        
        // Mapeo recursivo de las respuestas (replies)
        List<CommentResponseDTO> repliesDTO = (entity.getReplies() != null)
            ? entity.getReplies().stream()
                .map(this::toResponseDTO) // Se llama a sí mismo para cada hijo
                .collect(Collectors.toList())
            : new ArrayList<>();

        return new CommentResponseDTO(
            entity.getId(),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.isResolved(),        // Nuevo campo
            entity.getPage().getId(),
            entity.getUser().getId(),
            entity.getUser().getUsername(),
            repliesDTO                  // Lista de respuestas anidadas
        );
    }

    @Override
    public Comment toEntity(CommentRequestDTO dto) {
        if (dto == null) return null;
        
        // El mapeo de parentId, Page y User se gestiona en el Service
        // para asegurar que los objetos existan en la base de datos.
        return Comment.builder()
            .content(dto.content())
            .build();
    }
}