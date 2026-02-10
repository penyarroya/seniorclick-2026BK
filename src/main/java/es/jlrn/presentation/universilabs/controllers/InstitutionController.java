// package es.jlrn.presentation.universilabs.controllers;

// import es.jlrn.presentation.universilabs.dtos.institutions.InstitutionDTO;
// import es.jlrn.presentation.universilabs.services.interfaces.InstitutionService;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// import org.springframework.data.web.PageableDefault;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// import java.util.List;

// @RestController
// @RequestMapping("/api/universilabs/institutions")
// @RequiredArgsConstructor
// public class InstitutionController {
// //
//     private final InstitutionService institutionService;

//     // ====== GET PAGINATED ======
//     @GetMapping("/page") // Asegúrate que coincida con lo que pongas en Angular
//     public ResponseEntity<Page<InstitutionDTO>> getPage(
//             @PageableDefault(size = 10) Pageable pageable
//             // Aquí podrías añadir parámetros para filtros si tu service los soporta
//     ) {
//         // Debes asegurarte que tu interfaz InstitutionService tenga este método
//         return ResponseEntity.ok(institutionService.getAllPaginated(pageable));
//     }

//     // ====== GET ALL ======
//     @GetMapping
//     public ResponseEntity<List<InstitutionDTO>> getAll() {
//         return ResponseEntity.ok(institutionService.getAll());
//     }

//     // ====== GET BY ID ======
//     @GetMapping("/{id}")
//     public ResponseEntity<InstitutionDTO> getById(@PathVariable Long id) {
//         return ResponseEntity.ok(institutionService.getById(id));
//     }

//     // ====== GET BY NAME ======
//     @GetMapping("/current")
//     public ResponseEntity<InstitutionDTO> getCurrentInstitution() {
//         return ResponseEntity.ok(institutionService.getCurrent());
//     }


//     // ====== CREATE ======
//     @PostMapping
//     public ResponseEntity<InstitutionDTO> create(@Valid @RequestBody InstitutionDTO dto) {
//         InstitutionDTO created = institutionService.create(dto);
//         return ResponseEntity.status(HttpStatus.CREATED).body(created);
//     }

//     // ====== UPDATE ======
//     @PutMapping("/{id}")
//     public ResponseEntity<InstitutionDTO> update(
//             @PathVariable Long id,
//             @Valid @RequestBody InstitutionDTO dto
//     ) {
//         InstitutionDTO updated = institutionService.update(id, dto);
//         return ResponseEntity.ok(updated);
//     }

//     // ====== DELETE ======
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> delete(@PathVariable Long id) {
//         institutionService.delete(id);
//         return ResponseEntity.noContent().build();
//     }
// }

package es.jlrn.presentation.universilabs.controllers;

import es.jlrn.presentation.universilabs.dtos.institutions.InstitutionDTO;
import es.jlrn.presentation.universilabs.services.interfaces.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universilabs/institutions")
@RequiredArgsConstructor
public class InstitutionController {
//
    private final InstitutionService institutionService;

    // InstitutionController.java
    @GetMapping("/page")
    public ResponseEntity<Page<InstitutionDTO>> getPage(
            @PageableDefault(size = 10, sort = "name") Pageable pageable,
            @RequestParam(required = false) String global, // <--- AÑADIR ESTO (Buscador neón)
            @RequestParam(required = false) String name,   // (Filtros específicos si los usas)
            @RequestParam(required = false) String email
    ) {
        // IMPORTANTE: Ahora pasamos los 4 parámetros al Service
        return ResponseEntity.ok(institutionService.getAllPaginated(pageable, global, name, email));
    }

    // ====== GET ALL ======
    @GetMapping
    public ResponseEntity<List<InstitutionDTO>> getAll() {
        return ResponseEntity.ok(institutionService.getAll());
    }

    // ====== GET CURRENT (Añadir este método) ======
    @GetMapping("/current")
    public ResponseEntity<InstitutionDTO> getCurrentInstitution() {
        return ResponseEntity.ok(institutionService.getCurrent());
    }

    // ====== GET BY ID ======
    @GetMapping("/{id}")
    public ResponseEntity<InstitutionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(institutionService.getById(id));
    }

    // ====== CREATE ======
    @PostMapping
    public ResponseEntity<InstitutionDTO> create(@Valid @RequestBody InstitutionDTO dto) {
        InstitutionDTO created = institutionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ====== UPDATE ======
    @PutMapping("/{id}")
    public ResponseEntity<InstitutionDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody InstitutionDTO dto
    ) {
        InstitutionDTO updated = institutionService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ====== DELETE ======
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        institutionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // InstitutionController.java
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        return ResponseEntity.ok(institutionService.existsByEmail(email));
    }

    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkName(@RequestParam String name) {
        return ResponseEntity.ok(institutionService.existsByName(name));
    }
}
