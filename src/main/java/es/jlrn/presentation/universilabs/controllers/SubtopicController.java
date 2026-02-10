package es.jlrn.presentation.universilabs.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicRequestDTO;
import es.jlrn.presentation.universilabs.dtos.subtopics.SubtopicResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.ISubtopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subtopics")
@RequiredArgsConstructor
public class SubtopicController {
//
    private final ISubtopicService subtopicService;

    @GetMapping
    public ResponseEntity<Page<SubtopicResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(subtopicService.findAll(pageable));
    }

    /**
     * Obtiene todos los subtemas de un tema específico.
     * Ejemplo: GET /api/subtopics/topic/1
     */
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<SubtopicResponseDTO>> getSubtopicsByTopic(@PathVariable Long topicId) {
        List<SubtopicResponseDTO> subtopics = subtopicService.findByTopicId(topicId);
        return ResponseEntity.ok(subtopics);
    }

    /**
     * Obtiene el detalle de un subtema por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubtopicResponseDTO> getSubtopicById(@PathVariable Long id) {
        return ResponseEntity.ok(subtopicService.findById(id));
    }

    /**
     * Crea un nuevo subtema dentro de un tema.
     */
    @PostMapping
    public ResponseEntity<SubtopicResponseDTO> createSubtopic(
            @Valid @RequestBody SubtopicRequestDTO subtopicRequestDTO) {
        SubtopicResponseDTO savedSubtopic = subtopicService.save(subtopicRequestDTO);
        return new ResponseEntity<>(savedSubtopic, HttpStatus.CREATED);
    }

    /**
     * Actualiza un subtema existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SubtopicResponseDTO> updateSubtopic(
            @PathVariable Long id,
            @Valid @RequestBody SubtopicRequestDTO subtopicRequestDTO) {
        return ResponseEntity.ok(subtopicService.update(id, subtopicRequestDTO));
    }

    /**
     * Elimina un subtema y sus páginas asociadas (Cascade).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubtopic(@PathVariable Long id) {
        subtopicService.delete(id);
        return ResponseEntity.noContent().build();
    }
}