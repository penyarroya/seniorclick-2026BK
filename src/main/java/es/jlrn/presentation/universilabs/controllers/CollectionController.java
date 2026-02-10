package es.jlrn.presentation.universilabs.controllers;

import org.springframework.data.domain.Page;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.ICollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {
//
    private final ICollectionService collectionService;

    /**
     * ENDPOINT PARA LA TABLA DE MANTENIMIENTO
     * Este es el que llama Angular desde el método list() del service.
     */
    @GetMapping
    public ResponseEntity<Page<CollectionResponseDTO>> getAllCollections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(collectionService.findAllPaginated(page, size, search));
    }

    /**
     * Obtiene todas las colecciones de un proyecto específico.
     * Ejemplo: GET /api/collections/project/1
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<CollectionResponseDTO>> getCollectionsByProject(@PathVariable Long projectId) {
        List<CollectionResponseDTO> collections = collectionService.findByProjectId(projectId);
        return ResponseEntity.ok(collections);
    }

    /**
     * Obtiene el detalle de una colección por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CollectionResponseDTO> getCollectionById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.findById(id));
    }

    /**
     * Crea una nueva colección dentro de un proyecto.
     */
    @PostMapping
    public ResponseEntity<CollectionResponseDTO> createCollection(
            @Valid @RequestBody CollectionRequestDTO collectionRequestDTO) {
        CollectionResponseDTO savedCollection = collectionService.save(collectionRequestDTO);
        return new ResponseEntity<>(savedCollection, HttpStatus.CREATED);
    }

    /**
     * Actualiza una colección existente (ej. cambiarle el nombre).
     */
    @PutMapping("/{id}")
    public ResponseEntity<CollectionResponseDTO> updateCollection(
            @PathVariable Long id,
            @Valid @RequestBody CollectionRequestDTO collectionRequestDTO) {
        return ResponseEntity.ok(collectionService.update(id, collectionRequestDTO));
    }

    /**
     * Elimina una colección y todos sus temas asociados (Cascade).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}