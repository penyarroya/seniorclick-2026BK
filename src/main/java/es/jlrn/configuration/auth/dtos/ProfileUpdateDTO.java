package es.jlrn.configuration.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDTO {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String firstName;
    
    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;
    
    private String phone;
}