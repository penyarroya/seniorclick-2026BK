package es.jlrn.presentation.universilabs.mappers;

import org.springframework.stereotype.Component;

import es.jlrn.persistence.models.universilabs.models.Comment;
import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ICommentMapper;

@Component
public class CommentMapperImpl implements ICommentMapper {
//    
    @Override
    public CommentResponseDTO toResponseDTO(Comment entity) {
        if (entity == null) return null;
        
        return new CommentResponseDTO(
            entity.getId(),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getPage().getId(),
            entity.getUser().getId(),
            entity.getUser().getUsername() // Extraemos el username para el DTO
        );
    }

    @Override
    public Comment toEntity(CommentRequestDTO dto) {
        if (dto == null) return null;
        
        // Solo mapeamos el contenido; las relaciones (Page y User) 
        // se asignan en el Service tras validarlas en la DB.
        return Comment.builder()
            .content(dto.content())
            .build();
    }
}