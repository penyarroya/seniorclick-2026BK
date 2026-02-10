// package es.jlrn.presentation.dtos.Usuarios;

// import lombok.*;
// import jakarta.validation.constraints.*;
// import java.util.HashSet;
// import java.util.Set;
// import es.jlrn.persistence.enums.Role;

// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class UserCreateDTO {
// //
//     @NotBlank(message = "El nombre de usuario no puede estar vacío")
//     @Size(max = 40, message = "El nombre de usuario no puede exceder los 40 caracteres")
//     private String username;

//     @NotBlank(message = "La contraseña no puede estar vacía")
//     @Size(min = 9, message = "La contraseña debe tener al menos 9 caracteres")
//     private String password;

//     @NotBlank(message = "El correo electrónico no puede estar vacío")
//     @Email(message = "El correo electrónico debe tener un formato válido")
//     @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
//     private String email;

//     @Builder.Default
//     private Boolean activo = true;

//     // Roles opcionales al crear usuario (si no se pasan, se asignará USER por defecto)
//     @Builder.Default
//     private Set<Role> roles = new HashSet<>();
// }

package es.jlrn.presentation.users.dtos.Usuarios;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;
import es.jlrn.persistence.enums.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    //
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 40, message = "El nombre de usuario no puede exceder los 40 caracteres")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 9, message = "La contraseña debe tener al menos 9 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{9,}$",
        message = "La contraseña debe tener al menos 9 caracteres, incluyendo 1 mayúscula, 1 minúscula, 1 dígito y 1 carácter especial"
    )
    private String password;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", 
        message = "El correo electrónico debe tener un formato válido (ejemplo@dominio.com)")
    @Size(max = 150, message = "El correo electrónico no puede exceder los 150 caracteres")
    private String email;

    // --- NUEVOS CAMPOS PARA EL PERFIL ---
    @NotBlank(message = "El nombre no puede estar vacío")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    @Pattern(
        regexp = "^(\\+\\d{1,3}[- ]?)?\\d{7,15}$",
        message = "El teléfono debe tener un formato válido"
    )
    private String phone;
    // ------------------------------------

    @Builder.Default
    private Boolean activo = true;

    // Roles opcionales al crear usuario (si no se pasan, se asignará USER por defecto)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}

