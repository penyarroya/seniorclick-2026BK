package es.jlrn.presentation.universilabs.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.jlrn.presentation.universilabs.dtos.resources.ResourceDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ResourceController {
//
    private final IResourceService resourceService;

    // Crear un nuevo recurso (Imagen, Vídeo, etc.)
    @PostMapping
    public ResponseEntity<ResourceDTO> create(@Valid @RequestBody ResourceDTO dto) {
        return new ResponseEntity<>(resourceService.create(dto), HttpStatus.CREATED);
    }

    // Obtener todos los recursos de una página específica
    @GetMapping("/page/{pageId}")
    public ResponseEntity<List<ResourceDTO>> getByPage(@PathVariable Long pageId) {
        return ResponseEntity.ok(resourceService.findByPageId(pageId));
    }

    // Obtener un recurso individual por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ResourceDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(resourceService.findAll(pageable));
    }

    // Actualizar un recurso
    @PutMapping("/{id}")
    public ResponseEntity<ResourceDTO> update(@PathVariable Long id, @Valid @RequestBody ResourceDTO dto) {
        return ResponseEntity.ok(resourceService.update(id, dto));
    }

    // Eliminar un recurso
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}