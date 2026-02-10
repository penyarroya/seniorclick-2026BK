package es.jlrn.presentation.universilabs.controllers;

import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressRequestDTO;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IUserProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-progress")
@RequiredArgsConstructor
public class UserProgressController {

    private final IUserProgressService progressService;

    // 
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserProgressResponseDTO>> getProgressByUserId(@PathVariable Long userId) {
        // Aquí llamamos al método que ya tienes listo
        List<UserProgressResponseDTO> response = progressService.getAllProgressByUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Completa una página y sugiere la siguiente.
     * Útil para el botón "Siguiente Lección" en la interfaz.
     */
    @PostMapping("/complete")
    public ResponseEntity<UserProgressResponseDTO> completePage(
            @RequestParam Long userId,
            @RequestParam Long projectId,
            @RequestParam Long pageId) {
        // Asumiendo que añadimos completePage a IUserProgressService
        return ResponseEntity.ok(progressService.completePage(userId, projectId, pageId));
    }

    @PostMapping
    public ResponseEntity<UserProgressResponseDTO> updateProgress(
            @Valid @RequestBody UserProgressRequestDTO dto) {
        return ResponseEntity.ok(progressService.updateProgress(dto));
    }

    /**
     * Actualiza el mensaje de motivación personalizado desde el panel de mantenimiento.
     */
    @PatchMapping("/motivation")
    public ResponseEntity<UserProgressResponseDTO> updateMotivation(
            @RequestParam Long userId,
            @RequestParam Long pageId,
            @RequestBody Map<String, String> body) {
        
        String newMessage = body.get("motivationMessage");
        UserProgressResponseDTO updated = progressService.updateMotivationMessage(userId, pageId, newMessage);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/user/{userId}/page/{pageId}")
    public ResponseEntity<?> getProgress( // Usamos ? para permitir distintos cuerpos
            @PathVariable Long userId, 
            @PathVariable Long pageId) {
        UserProgressResponseDTO progress = progressService.getProgress(userId, pageId);
        if (progress == null) {
            return ResponseEntity.noContent().build(); // Devuelve 204 en lugar de error
        }
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/resume")
    public ResponseEntity<?> resumeCourse(
            @RequestParam Long userId, 
            @RequestParam Long projectId) {
        UserProgressResponseDTO progress = progressService.resumeCourse(userId, projectId);
        if (progress == null) {
            // Si no hay progreso iniciado, devolvemos un objeto vacío coherente o 204
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(progress);
    }
    @GetMapping("/percentage")
    public ResponseEntity<Map<String, Double>> getPercentage(
            @RequestParam Long userId, 
            @RequestParam Long projectId) {
        Double percentage = progressService.getCompletionPercentage(userId, projectId);
        return ResponseEntity.ok(Map.of("percentage", percentage));
    }

    @GetMapping("/certificate")
    public ResponseEntity<Map<String, Object>> getCertificate(
            @RequestParam Long userId, 
            @RequestParam Long projectId) {
        boolean completed = progressService.isProjectCompleted(userId, projectId);
        
        if (!completed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                    "message", "Aún no has terminado todas las lecciones. ¡Sigue adelante!",
                    "completed", false
                ));
        }

        return ResponseEntity.ok(Map.of(
            "message", "¡FELICIDADES! Has completado con éxito el curso.",
            "date", LocalDateTime.now(),
            "status", "CERTIFIED",
            "completed", true
        ));
    }
}