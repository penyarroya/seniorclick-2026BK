 //package es.jlrn.presentation.universilabs.controllers;

// import es.jlrn.presentation.universilabs.dtos.projects.ProjectRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.projects.ProjectResponseDTO;
// import es.jlrn.presentation.universilabs.services.interfaces.IProjectService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.web.PageableDefault; // Importante para definir valores por defecto

// @RestController
// @RequestMapping("/api/projects")
// @RequiredArgsConstructor
// public class ProjectController {

//     private final IProjectService projectService;

//     @GetMapping
//     public ResponseEntity<Page<ProjectResponseDTO>> getAll(
//             @RequestParam(required = false) String search, 
//             @PageableDefault(size = 10) Pageable pageable) {
//         // Aquí projectService es la interfaz inyectada, no 'this'
//         return ResponseEntity.ok(projectService.findAll(search, pageable));
//     }
    
//     @GetMapping("/all")
//     public ResponseEntity<List<ProjectResponseDTO>> getAllWithoutPagination() {
//         // Necesitarás crear este método en tu Service o usar uno que devuelva List
//         return ResponseEntity.ok(projectService.findAllList()); 
//     }

//     // En ProjectController.java
//     @GetMapping("/all")
//     public ResponseEntity<List<ProjectResponseDTO>> getAllActive() {
//         // Ahora solo devuelve los que tienen activo = true
//         return ResponseEntity.ok(projectService.findAllActiveList()); 
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
//         return ResponseEntity.ok(projectService.findById(id));
//     }

//     @PostMapping
//     public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
//         ProjectResponseDTO savedProject = projectService.save(projectRequestDTO);
//         return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<ProjectResponseDTO> updateProject(
//             @PathVariable Long id, 
//             @Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
//         return ResponseEntity.ok(projectService.update(id, projectRequestDTO));
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
//         projectService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }

package es.jlrn.presentation.universilabs.controllers;

import es.jlrn.presentation.universilabs.dtos.projects.ProjectRequestDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectResponseDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectStructureDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    /**
     * Endpoint para la tabla principal.
     * Soporta búsqueda opcional y paginación.
     */
    @GetMapping
    public ResponseEntity<Page<ProjectResponseDTO>> getAll(
            @RequestParam(required = false) String search, 
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(projectService.findAll(search, pageable));
    }
    
    /**
     * Endpoint para selectores (dropdowns) en Angular.
     * Devuelve solo proyectos activos sin paginación.
     */
    @GetMapping("/all")
    public ResponseEntity<List<ProjectResponseDTO>> getAllActive() {
        return ResponseEntity.ok(projectService.findAllActiveList()); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @GetMapping("/{id}/structure")
    public ResponseEntity<ProjectStructureDTO> getProjectStructure(@PathVariable Long id) {
        ProjectStructureDTO structure = projectService.findStructureById(id);
        return ResponseEntity.ok(structure);
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectResponseDTO savedProject = projectService.save(projectRequestDTO);
        return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id, 
            @Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        return ResponseEntity.ok(projectService.update(id, projectRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}