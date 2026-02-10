// package es.jlrn.presentation.universilabs.controllers;

// import org.springframework.data.domain.Pageable;
// import es.jlrn.presentation.universilabs.dtos.pages.PageDetailDTO;
// import es.jlrn.presentation.universilabs.dtos.pages.PageRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;
// import es.jlrn.presentation.universilabs.services.interfaces.IPageService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/pages")
// @RequiredArgsConstructor
// public class PageController {
// //
//     private final IPageService pageService;

//     /**
//      * Este endpoint es el que llama el MaintenanceLayout de Angular.
//      * Spring inyecta automáticamente 'page' y 'size' desde la URL en el objeto pageable.
//      */
//     @GetMapping
//     public ResponseEntity<org.springframework.data.domain.Page<PageResponseDTO>> getAll(Pageable pageable) {
//         return ResponseEntity.ok(pageService.findAll(pageable));
//     }

//     /**
//      * Obtiene todas las páginas de un subtema específico.
//      * GET /api/pages/subtopic/{subtopicId}
//      */
//     @GetMapping("/subtopic/{subtopicId}")
//     public ResponseEntity<List<PageResponseDTO>> getPagesBySubtopic(@PathVariable Long subtopicId) {
//         return ResponseEntity.ok(pageService.findBySubtopicId(subtopicId));
//     }

//     /**
//      * Obtiene una página por su ID.
//      * GET /api/pages/{id}
//      */
//     @GetMapping("/{id}")
//     public ResponseEntity<PageResponseDTO> getPageById(@PathVariable Long id) {
//         return ResponseEntity.ok(pageService.findById(id));
//     }

//     /**
//      * Crea una nueva página (Contenido).
//      * POST /api/pages
//      */
//     @PostMapping
//     public ResponseEntity<PageResponseDTO> createPage(@Valid @RequestBody PageRequestDTO pageRequestDTO) {
//         PageResponseDTO savedPage = pageService.save(pageRequestDTO);
//         return new ResponseEntity<>(savedPage, HttpStatus.CREATED);
//     }

//     /**
//      * Actualiza el contenido o el subtema de una página.
//      * PUT /api/pages/{id}
//      */
//     @PutMapping("/{id}")
//     public ResponseEntity<PageResponseDTO> updatePage(
//             @PathVariable Long id,
//             @Valid @RequestBody PageRequestDTO pageRequestDTO) {
//         return ResponseEntity.ok(pageService.update(id, pageRequestDTO));
//     }

//     /**
//      * Obtiene el detalle completo (Contenido + Recursos + Progreso)
//      * GET /api/pages/{id}/detail?userId=1
//      */
//     @GetMapping("/{id}/detail")
//     public ResponseEntity<PageDetailDTO> getPageDetail(
//         @PathVariable Long id, 
//         @RequestParam Long userId) {
//     return ResponseEntity.ok(pageService.getPageDetailForUser(id, userId));
//     }

//     /**
//      * Elimina una página.
//      * DELETE /api/pages/{id}
//      */
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deletePage(@PathVariable Long id) {
//         pageService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }

package es.jlrn.presentation.universilabs.controllers;

import org.springframework.data.domain.Pageable;
import es.jlrn.presentation.universilabs.dtos.pages.PageDetailDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageRequestDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Ajusta según tus necesidades de Angular
public class PageController {

    private final IPageService pageService;

    @GetMapping
    public ResponseEntity<org.springframework.data.domain.Page<PageResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(pageService.findAll(pageable));
    }

    @GetMapping("/subtopic/{subtopicId}")
    public ResponseEntity<List<PageResponseDTO>> getPagesBySubtopic(@PathVariable Long subtopicId) {
        return ResponseEntity.ok(pageService.findBySubtopicId(subtopicId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageResponseDTO> getPageById(@PathVariable Long id) {
        return ResponseEntity.ok(pageService.findById(id));
    }

    /**
     * Crea una nueva página. 
     * El PageRequestDTO ahora incluye el campo 'format' (HTML, PLAIN_TEXT, etc.)
     */
    @PostMapping
    public ResponseEntity<PageResponseDTO> createPage(@Valid @RequestBody PageRequestDTO pageRequestDTO) {
        return new ResponseEntity<>(pageService.save(pageRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PageResponseDTO> updatePage(
            @PathVariable Long id,
            @Valid @RequestBody PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(pageService.update(id, pageRequestDTO));
    }

    /**
     * Endpoint crítico para la visualización del alumno.
     * Devuelve el formato, contenido, recursos y si es la última página.
     */
    @GetMapping("/{id}/detail")
    public ResponseEntity<PageDetailDTO> getPageDetail(
            @PathVariable Long id, 
            @RequestParam Long userId) {
        return ResponseEntity.ok(pageService.getPageDetailForUser(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable Long id) {
        pageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}