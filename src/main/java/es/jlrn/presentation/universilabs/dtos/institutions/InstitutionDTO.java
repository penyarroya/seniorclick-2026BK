package es.jlrn.presentation.universilabs.dtos.institutions;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstitutionDTO {
//
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String name;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String address;

    @Size(max = 255, message = "El sitio web no puede superar los 255 caracteres")
    @URL(message = "El sitio web debe ser una URL válida")
    private String website;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotBlank(message = "El email es obligatorio")
    @Pattern(
        regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
        message = "El formato del email no es válido"
    )
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

}
