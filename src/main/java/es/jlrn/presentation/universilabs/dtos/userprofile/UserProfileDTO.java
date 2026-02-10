package es.jlrn.presentation.universilabs.dtos.userprofile;

import jakarta.validation.constraints.*;

public record UserProfileDTO(
//    
    Long userId,

    @NotBlank(message = "El nombre es obligatorio")
    String firstName,
    
    @NotBlank(message = "El apellido es obligatorio")
    String lastName,
    
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{7,15}$", message = "Formato de teléfono inválido")
    String phone,
    
    @Size(max = 500)
    String avatarUrl
) {}