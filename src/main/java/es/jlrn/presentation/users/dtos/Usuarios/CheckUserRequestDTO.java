package es.jlrn.presentation.users.dtos.Usuarios;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckUserRequestDTO {
//    
    @Email(message = "El correo electrónico debe tener un formato válido")
    private String email;
}