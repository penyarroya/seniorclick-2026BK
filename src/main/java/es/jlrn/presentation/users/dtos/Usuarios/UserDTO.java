package es.jlrn.presentation.users.dtos.Usuarios;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
//    
    private Long id;
    private String username;
    private String email;
    // 🔹 NUEVOS CAMPOS DEL PERFIL
    private String firstName;
    private String lastName;
    private String phone;
    
    private Boolean activo;
    private Boolean emailVerified; // ✅ nuevo campo
    private String verificationCode;        // ✅ nuevo
    private LocalDateTime verificationCodeExpiry; // ✅ nuevo
    private Set<String> roles;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaActualizacion;
}
