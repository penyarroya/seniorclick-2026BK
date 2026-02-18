package es.jlrn.presentation.universilabs.dtos.userprofile;

import jakarta.validation.constraints.*;
import lombok.Builder;


@Builder
public record UserProfileDTO(
//    
    Long id,
    Long userId,
    @NotBlank(message = "El nombre es obligatorio")
    String firstName,
    @NotBlank(message = "El apellido es obligatorio")
    String lastName,
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{7,15}$", message = "Formato de teléfono inválido")
    String phone,
    @Size(max = 500)
    String avatarUrl,
    // --- ESTOS SON LOS CAMPOS QUE TE FALTABAN ---
    String nombre,        // Para el buscador y listas de Angular
    Boolean activo,       // Para los toggles de estado
    String description    // Para información adicional en el layout
) {}