package es.jlrn.presentation.universilabs.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentResponseDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IEnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EnrollmentController {
//
    private final IEnrollmentService enrollmentService;

    /**
     * REQUERIDO PARA EL MAINTENANCE LAYOUT (TABLA)
     * Maneja la paginación automática desde Angular
     */
    @GetMapping
    public ResponseEntity<Page<EnrollmentResponseDTO>> getAll(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(enrollmentService.findAll(pageable));
    }

    /**
     * OPCIONAL: Retorna todos sin paginar
     */
    @GetMapping("/all")
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllList() {
        return ResponseEntity.ok(enrollmentService.getAllList());
    }
    
    @GetMapping("/check-access/{projectId}")
    public ResponseEntity<Boolean> checkAccess(
            @AuthenticationPrincipal UserEntity user, 
            @PathVariable Long projectId) {
        
        // Si el usuario no está logueado, Spring inyectará null
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }

        // Llamamos al service que acabamos de actualizar
        boolean isEnrolled = enrollmentService.isUserEnrolled(user.getId(), projectId);
        
        return ResponseEntity.ok(isEnrolled);
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enroll(@Valid @RequestBody EnrollmentRequestDTO dto) {
        return new ResponseEntity<>(enrollmentService.enroll(dto), HttpStatus.CREATED);
    }

    /**
     * Endpoint para que el usuario autenticado se inscriba a sí mismo
     */
    @PostMapping("/enroll-me/{projectId}")
    public ResponseEntity<EnrollmentResponseDTO> enrollMe(
            @AuthenticationPrincipal UserEntity user, 
            @PathVariable Long projectId) {
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Al ser un Record, pasamos los datos directamente al constructor
        // Por defecto, asignamos el rol "LEARNER" al estudiante que se inscribe solo
        EnrollmentRequestDTO dto = new EnrollmentRequestDTO(
            user.getId(), 
            projectId, 
            "LEARNER" 
        );

        return new ResponseEntity<>(enrollmentService.enroll(dto), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getMyProjects(@PathVariable Long userId) {
        return ResponseEntity.ok(enrollmentService.getProjectsByUser(userId));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getProjectUsers(@PathVariable Long projectId) {
        return ResponseEntity.ok(enrollmentService.getUsersByProject(projectId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unenroll(@PathVariable Long id) {
        enrollmentService.unenroll(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para que el usuario autenticado se desvincule de un proyecto
     */
    @DeleteMapping("/unenroll-me/{projectId}")
    public ResponseEntity<Void> unenrollMe(
            @AuthenticationPrincipal UserEntity user, 
            @PathVariable Long projectId) {
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Buscamos la inscripción para este usuario y proyecto
        // Nota: Asegúrate de añadir este método al Service si decides usarlo
        enrollmentService.unenrollByUserAndProject(user.getId(), projectId);
        
        return ResponseEntity.noContent().build();
    }
}