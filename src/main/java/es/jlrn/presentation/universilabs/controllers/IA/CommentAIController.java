// package es.jlrn.presentation.universilabs.controllers.IA;

// import es.jlrn.persistence.models.universilabs.models.Comment;
// import es.jlrn.presentation.universilabs.dtos.IA.IASuggestionResponse;
// import es.jlrn.presentation.universilabs.services.IA.AIService;
// import es.jlrn.presentation.universilabs.services.impl.CommentServiceImpl;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/comments")
// @CrossOrigin(origins = "http://localhost:4200")
// public class CommentAIController {
// //
//     private final AIService aiService;
//     private final CommentServiceImpl commentService;

//     // Constructor único: Spring inyecta automáticamente las dependencias
//     public CommentAIController(AIService aiService, CommentServiceImpl commentService) {
//         this.aiService = aiService;
//         this.commentService = commentService;
//     }

//     @GetMapping("/{id}/ai-suggestion")
//     public ResponseEntity<IASuggestionResponse> getAiSuggestion(@PathVariable Long id) {
        
//         // 1. Buscamos el comentario. 
//         // Como tu findById ahora lanza ResourceNotFoundException, 
//         // Spring devolverá automáticamente un 404 si no existe.
//         Comment comment = commentService.findById(id);

//         // 2. Extraemos datos
//         String preguntaAlumno = comment.getContent();
        
//         // Manejo de seguridad para la relación con Page
//         String tituloLeccion = (comment.getPage() != null) 
//                                 ? comment.getPage().getTitle() 
//                                 : "Lección General";

//         // 3. Llamada a Gemini
//         String sugerenciaTexto = aiService.generateResponse(preguntaAlumno, tituloLeccion);

//         // 4. Respuesta al Frontend
//         return ResponseEntity.ok(new IASuggestionResponse(sugerenciaTexto));
//     }
// }

package es.jlrn.presentation.universilabs.controllers.IA;

import es.jlrn.persistence.models.universilabs.models.Comment;
import es.jlrn.presentation.universilabs.dtos.IA.IASuggestionResponse;
import es.jlrn.presentation.universilabs.services.IA.AIService;
import es.jlrn.presentation.universilabs.services.impl.CommentServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200")
public class CommentAIController {
//
    private final AIService aiService;
    private final CommentServiceImpl commentService;

    public CommentAIController(AIService aiService, CommentServiceImpl commentService) {
        this.aiService = aiService;
        this.commentService = commentService;
    }

    @GetMapping("/{id}/ai-suggestion")
    public ResponseEntity<IASuggestionResponse> getAiSuggestion(@PathVariable Long id) {
        
        // 1. Obtener el comentario (Lanzará 404 si no existe gracias a tu Service)
        Comment comment = commentService.findById(id);

        // 2. Preparar datos para la IA
        String question = comment.getContent();
        String context = (comment.getPage() != null) ? comment.getPage().getTitle() : "Tema General";

        // 3. Validación defensiva: Si el comentario está vacío, no molestamos a la API
        if (question == null || question.isBlank()) {
            return ResponseEntity.ok(new IASuggestionResponse("El comentario no tiene contenido para analizar."));
        }

        // 4. Obtener respuesta de Gemini
        // Si ocurre un error de red o de API, el AIService lanzará una excepción 
        // que será capturada por un GlobalExceptionHandler (o devolverá un 500)
        String suggestionText = aiService.generateResponse(question, context);

        return ResponseEntity.ok(new IASuggestionResponse(suggestionText));
    }
}