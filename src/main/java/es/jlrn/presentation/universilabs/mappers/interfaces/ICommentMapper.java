package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.Comment;
import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;

public interface ICommentMapper {
//    
    CommentResponseDTO toResponseDTO(Comment entity);
    Comment toEntity(CommentRequestDTO dto);
}