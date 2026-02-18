// package es.jlrn.presentation.universilabs.controllers;

// import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;
// import es.jlrn.presentation.universilabs.services.interfaces.IUserProfileService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// import org.springframework.data.web.PageableDefault;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.security.core.Authentication;

// @RestController
// @RequestMapping("/api/profiles")
// @RequiredArgsConstructor
// public class UserProfileController {
// //
//     private final IUserProfileService profileService;

//     @GetMapping("/{userId}")
//     public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
//         return ResponseEntity.ok(profileService.getProfile(userId));
//     }

//     @GetMapping
//     public ResponseEntity<Page<UserProfileDTO>> getAllProfiles(
//             @PageableDefault(size = 10) Pageable pageable) {
//         return ResponseEntity.ok(profileService.findAll(pageable));
//     }

//     @GetMapping("/me")
//     public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
//         String identifier = authentication.getName();
        
//         try {
//             if (identifier.contains("@")) {
//                 // Usamos el Builder que ya tienes en el Record
//                 UserProfileDTO emptyProfile = UserProfileDTO.builder()
//                     .userId(null)
//                     .firstName("")
//                     .lastName("")
//                     .phone("")
//                     .avatarUrl("")
//                     .build();

//                 return ResponseEntity.ok(emptyProfile); 
//             } else {
//                 Long userId = Long.parseLong(identifier);
//                 return ResponseEntity.ok(profileService.getProfile(userId));
//             }
//         } catch (Exception e) {
//             return ResponseEntity.noContent().build(); 
//         }
//     }

//     @PutMapping("/me")
//     public ResponseEntity<UserProfileDTO> updateMyProfile(
//             Authentication authentication, 
//             @Valid @RequestBody UserProfileDTO dto) {
        
//         // 1. Extraemos el ID del token
//         Long userId = Long.parseLong(authentication.getName());
        
//         // 2. Llamamos al service para actualizar
//         return ResponseEntity.ok(profileService.updateProfile(userId, dto));
//     }


//     /**
//      * CREAR: Vincula un nuevo perfil a un usuario existente.
//      * El userId en la URL define a quién pertenece este perfil.
//      */
//     // @PostMapping("/{userId}")
//     // public ResponseEntity<UserProfileDTO> createProfile(
//     //         @PathVariable Long userId,
//     //         @Valid @RequestBody UserProfileDTO dto) {
//     //     // Nota: Asegúrate de añadir 'createProfile' a tu interfaz IUserProfileService
//     //     return ResponseEntity.status(HttpStatus.CREATED)
//     //             .body(profileService.createProfile(userId, dto));
//     // }

//     @PostMapping("/{userId}")
//     public ResponseEntity<?> create(@PathVariable Long userId, @RequestBody UserProfileDTO dto) {
//         try {
//             return ResponseEntity.ok(profileService.createProfile(userId, dto));
//         } catch (Exception e) {
//             e.printStackTrace(); // <--- MIRA TU CONSOLA DE JAVA AHORA
//             return ResponseEntity.status(500).body("Error: " + e.getMessage());
//         }
//     }

//     @PutMapping("/{userId}")
//     public ResponseEntity<UserProfileDTO> updateProfile(
//             @PathVariable Long userId,
//             @Valid @RequestBody UserProfileDTO dto) {
//         return ResponseEntity.ok(profileService.updateProfile(userId, dto));
//     }

//     /**
//      * ELIMINAR: Borra el perfil asociado al usuario.
//      */
//     @DeleteMapping("/{userId}")
//     public ResponseEntity<Void> deleteProfile(@PathVariable Long userId) {
//         profileService.deleteProfile(userId); // Nota: Añadir a la interfaz y ServiceImpl
//         return ResponseEntity.noContent().build();
//     }
// }

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
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final IUserProfileService profileService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @GetMapping
    public ResponseEntity<Page<UserProfileDTO>> getAllProfiles(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(profileService.findAll(pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(Authentication authentication) {
        try {
            // Es mejor usar una lógica consistente para obtener el ID
            Long userId = Long.parseLong(authentication.getName());
            return ResponseEntity.ok(profileService.getProfile(userId));
        } catch (NumberFormatException e) {
            // Si el principal no es un ID (es un email/username), devolvemos contenido vacío o error
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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