package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;
import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;

public interface ICommentService {
//    
    List<CommentResponseDTO> findByPageId(Long pageId);
    
    CommentResponseDTO save(CommentRequestDTO dto);
    
    void delete(Long id);

    List<CommentResponseDTO> findAll();
}