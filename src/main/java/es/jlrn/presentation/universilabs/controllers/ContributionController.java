package es.jlrn.presentation.universilabs.controllers;

import es.jlrn.presentation.universilabs.dtos.contributions.ContributionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contributions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Asegúrate de tener configurado CORS para Angular
public class ContributionController {

    private final IContributionService contributionService;

    /**
     * Obtiene todas las contribuciones para el mantenimiento administrativo.
     * GET /api/contributions
     */
    @GetMapping
    public ResponseEntity<List<ContributionResponseDTO>> getAll() {
        return ResponseEntity.ok(contributionService.list());
    }

    /**
     * NUEVO MÉTODO PARA "MIS APORTACIONES" (Comunidad)
     * GET /api/contributions/user/{userId}
     * Este es el endpoint que llamará tu Angular para mostrar el historial del usuario.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ContributionResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(contributionService.getByUser(userId));
    }

    /**
     * Obtiene todas las contribuciones de una página específica.
     * GET /api/contributions/page/{pageId}
     */
    @GetMapping("/page/{pageId}")
    public ResponseEntity<List<ContributionResponseDTO>> getByPage(@PathVariable Long pageId) {
        return ResponseEntity.ok(contributionService.getByPage(pageId));
    }

    /**
     * Crea una nueva contribución (propuesta de contenido).
     * POST /api/contributions
     */
    @PostMapping
    public ResponseEntity<ContributionResponseDTO> create(
            @Valid @RequestBody ContributionRequestDTO dto) {
        return new ResponseEntity<>(contributionService.save(dto), HttpStatus.CREATED);
    }

    /**
     * Elimina una contribución por su ID.
     * DELETE /api/contributions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contributionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}