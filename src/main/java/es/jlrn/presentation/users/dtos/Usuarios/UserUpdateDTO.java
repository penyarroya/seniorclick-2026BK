package es.jlrn.presentation.users.dtos.Usuarios;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @Size(max = 40, message = "El nombre de usuario no puede exceder los 40 caracteres")
    private String username;

    @Email(message = "El correo electrónico debe tener un formato válido")
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    private String email;

    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).{9,}$",
        message = "La contraseña debe tener al menos 9 caracteres, incluyendo 1 mayúscula, 1 minúscula, 1 dígito y 1 carácter especial"
    )
    private String password;

    private Set<String> roles;

    private Boolean activo;

    // --- NUEVOS CAMPOS PARA EL PERFIL (Eliminan el error de compilación) ---
    private String firstName;

    private String lastName;

    private String phone;
}
