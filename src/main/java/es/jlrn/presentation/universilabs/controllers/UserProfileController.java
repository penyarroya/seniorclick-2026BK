package es.jlrn.presentation.universilabs.controllers;

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

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {
//
    private final IUserProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    // Añade esto a tu UserProfileController.java
    // @GetMapping
    // public ResponseEntity<org.springframework.data.domain.Page<UserProfileDTO>> getAllProfiles(
    //         @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
    //     // Nota: Debes añadir 'findAll' a tu interfaz IUserProfileService y su implementación
    //     return ResponseEntity.ok(profileService.findAll(pageable));
    // }

    @GetMapping
    public ResponseEntity<Page<UserProfileDTO>> getAllProfiles(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(profileService.findAll(pageable));
    }

    /**
     * CREAR: Vincula un nuevo perfil a un usuario existente.
     * El userId en la URL define a quién pertenece este perfil.
     */
    @PostMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> createProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileDTO dto) {
        // Nota: Asegúrate de añadir 'createProfile' a tu interfaz IUserProfileService
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(profileService.createProfile(userId, dto));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileDTO dto) {
        return ResponseEntity.ok(profileService.updateProfile(userId, dto));
    }

    /**
     * ELIMINAR: Borra el perfil asociado al usuario.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
        profileService.deleteProfile(userId); // Nota: Añadir a la interfaz y ServiceImpl
        return ResponseEntity.noContent().build();
    }
}