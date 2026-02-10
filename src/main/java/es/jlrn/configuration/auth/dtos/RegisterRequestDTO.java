// package es.jlrn.configuration.auth.dtos;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Pattern;
// import jakarta.validation.constraints.Size;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
// public class RegisterRequestDTO {
// //
//     @NotBlank(message = "El nombre de usuario no puede estar vacío")
//     @Size(max = 40, message = "El nombre de usuario no puede exceder los 40 caracteres")
//     private String username;

//     @NotBlank(message = "La contraseña no puede estar vacía")
//     @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
//     private String password;
    
//     @NotBlank(message = "El correo electrónico no puede estar vacío")
//     @Email(message = "El correo electrónico debe tener un formato válido")
//     @Pattern(
//         regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
//         message = "El correo electrónico debe tener un dominio válido, por ejemplo: usuario@dominio.com"
//     )
//     @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
//     private String email;

//     // --- NUEVOS CAMPOS PARA USER_PROFILE ---

//     @NotBlank(message = "El nombre es obligatorio")
//     private String firstName;

//     @NotBlank(message = "El apellido es obligatorio")
//     private String lastName;

//     private String phone; // Opcional, o añade @NotBlank si lo requieres

// }


package es.jlrn.configuration.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 40, message = "El nombre de usuario no puede exceder los 40 caracteres")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?\\\\|`~]).{9,}$",
        message = "La contraseña debe tener al menos 9 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
    )
    private String password;
    
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico debe tener un formato válido")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
        message = "El correo electrónico debe tener un dominio válido"
    )
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    private String email;

    // --- NUEVOS CAMPOS PARA USER_PROFILE (Corrigen el error de compilación) ---

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    private String phone;
}