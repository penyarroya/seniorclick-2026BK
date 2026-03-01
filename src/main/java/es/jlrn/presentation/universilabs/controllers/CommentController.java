// package es.jlrn.presentation.universilabs.controllers;

// import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
// import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/comments")
// @RequiredArgsConstructor
// public class CommentController {
// //
//     private final ICommentService commentService;

//     /**
//      * Obtiene todos los comentarios de una página específica.
//      * GET /api/comments/page/{pageId}
//      */
//     @GetMapping("/page/{pageId}")
//     public ResponseEntity<List<CommentResponseDTO>> getCommentsByPage(@PathVariable Long pageId) {
//         return ResponseEntity.ok(commentService.findByPageId(pageId));
//     }

//     /**
//      * Obtiene TODOS los comentarios (Vista de Admin).
//      * GET /api/comments
//      */
//     @GetMapping
//     public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
//         return ResponseEntity.ok(commentService.findAll());
//     }

//     /**
//      * Crea un nuevo comentario en una página.
//      * POST /api/comments
//      */
//     @PostMapping
//     public ResponseEntity<CommentResponseDTO> createComment(
//             @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
//         CommentResponseDTO savedComment = commentService.save(commentRequestDTO);
//         return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
//     }

//     /**
//      * Elimina un comentario por su ID.
//      * DELETE /api/comments/{id}
//      */
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
//         commentService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }

// package es.jlrn.presentation.universilabs.controllers;

// import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
// import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/comments")
// @RequiredArgsConstructor
// public class CommentController {
// //
//     private final ICommentService commentService;

//     /**
//      * Obtiene los hilos de comentarios de una página específica.
//      * Gracias al Service, solo devuelve los comentarios raíz con sus respuestas anidadas.
//      */
//     @GetMapping("/page/{pageId}")
//     public ResponseEntity<List<CommentResponseDTO>> getCommentsByPage(@PathVariable Long pageId) {
//         return ResponseEntity.ok(commentService.findByPageId(pageId));
//     }

//     /**
//      * Obtiene TODOS los comentarios (Vista de Admin).
//      */
//     @GetMapping
//     public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
//         return ResponseEntity.ok(commentService.findAll());
//     }

//     /**
//      * Crea un nuevo comentario o una respuesta (si se envía parentId).
//      */
//     @PostMapping
//     public ResponseEntity<CommentResponseDTO> createComment(
//             @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
//         CommentResponseDTO savedComment = commentService.save(commentRequestDTO);
//         return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
//     }

//     /**
//      * Cambia el estado de resolución de un comentario (marcar como resuelto/pendiente).
//      * PATCH es ideal aquí porque solo estamos modificando un atributo.
//      */
//     @PatchMapping("/{id}/toggle-resolved")
//     public ResponseEntity<Void> toggleResolved(@PathVariable Long id) {
//         commentService.toggleResolved(id);
//         return ResponseEntity.ok().build();
//     }

//     /**
//      * Obtiene solo los comentarios que no han sido resueltos.
//      * Útil para un "Dashboard de Soporte".
//      */
//     @GetMapping("/unresolved")
//     public ResponseEntity<List<CommentResponseDTO>> getUnresolvedComments() {
//         return ResponseEntity.ok(commentService.findUnresolved());
//     }

//     /**
//      * Elimina un comentario por su ID.
//      */
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
//         commentService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }

package es.jlrn.presentation.universilabs.controllers;

import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j // Para logs de peticiones si lo deseas
public class CommentController {
//
    private final ICommentService commentService;

    @GetMapping("/page/{pageId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByPage(@PathVariable Long pageId) {
        return ResponseEntity.ok(commentService.findByPageId(pageId));
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @Valid @RequestBody CommentRequestDTO commentRequestDTO) {
        // El Service lanzará 400 si está duplicado y el GlobalExceptionHandler lo capturará
        CommentResponseDTO savedComment = commentService.save(commentRequestDTO);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    /**
     * Mejora: Devolver el objeto actualizado ayuda al Frontend a refrescar el estado 
     * del comentario de forma instantánea.
     */
    @PatchMapping("/{id}/toggle-resolved")
    public ResponseEntity<Void> toggleResolved(@PathVariable Long id) {
        commentService.toggleResolved(id);
        return ResponseEntity.noContent().build(); 
    }

    @GetMapping("/unresolved")
    public ResponseEntity<List<CommentResponseDTO>> getUnresolvedComments() {
        return ResponseEntity.ok(commentService.findUnresolved());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Opcional: Vista global para el Admin
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getAllComments() {
        return ResponseEntity.ok(commentService.findAll());
    }
}