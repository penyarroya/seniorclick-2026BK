package es.jlrn.presentation.universilabs.controllers;

import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IUserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {
//
    private final IUserProfileService profileService;
    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @GetMapping
    public ResponseEntity<Page<UserProfileDTO>> getAllProfiles(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(profileService.findAll(pageable));
    }

    // @GetMapping("/me")
    // public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
    //     try {
    //         // Es mejor usar una lógica consistente para obtener el ID
    //         Long userId = Long.parseLong(authentication.getName());
    //         return ResponseEntity.ok(profileService.getProfile(userId));
    //     } catch (NumberFormatException e) {
    //         // Si el principal no es un ID (es un email/username), devolvemos contenido vacío o error
    //         return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    //     }
    // }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        String identifier = authentication.getName(); // Puede ser "Nino" o "20"
        
        try {
            // En lugar de parsear y fallar, vamos a dejar que el servicio decida
            // Si tu service/repo tiene un método findByUsername, úsalo.
            // Si no, lo más seguro es obtener el ID del usuario desde el objeto Principal
            
            Long userId;
            try {
                userId = Long.parseLong(identifier);
            } catch (NumberFormatException e) {
                // Si el nombre no es un número, es un username. 
                // Necesitamos buscar el ID de ese username primero.
                userId = userRepository.findByUsername(identifier)
                        .map(UserEntity::getId)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            }
            
            return ResponseEntity.ok(profileService.getProfile(userId));
            
        } catch (Exception e) {
            // Loguea el error para saber qué está pasando
            System.out.println("Error en /me para el identificador: " + identifier);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * CREAR/ACTUALIZAR: 
     * Dado que tu Service en 'createProfile' ya verifica si existe,
     * este endpoint es seguro para el primer alta tras el registro.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> create(
            @PathVariable Long userId, 
            @Valid @RequestBody UserProfileDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(userId, dto));
    }

    /**
     * ACTUALIZAR: 
     * Este es el método que usaste para el ID 34 y funcionó.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(profileService.updateProfile(userId, dto));
    }

    /**
     * ACTUALIZAR MI PERFIL:
     * Utiliza el ID del token de seguridad para llamar al service de update.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateMyProfile(
            Authentication authentication, 
            @Valid @RequestBody UserProfileDTO dto) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(profileService.updateProfile(userId, dto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        profileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}