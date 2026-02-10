package es.jlrn.presentation.universilabs.controllers;

import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
//
    private final ICommentService commentService;

    /**
     * Obtiene todos los comentarios de una página específica.
     * GET /api/comments/page/{pageId}
     */
    @GetMapping("/page/{pageId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPage(@PathVariable Long pageId) {
        return ResponseEntity.ok(commentService.findByPageId(pageId));
    }

    /**
     * Obtiene TODOS los comentarios (Vista de Admin).
     * GET /api/comments
     */
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        return ResponseEntity.ok(commentService.findAll());
    }

    /**
     * Crea un nuevo comentario en una página.
     * POST /api/comments
     */
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        CommentResponseDTO savedComment = commentService.save(commentRequestDTO);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    /**
     * Elimina un comentario por su ID.
     * DELETE /api/comments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}