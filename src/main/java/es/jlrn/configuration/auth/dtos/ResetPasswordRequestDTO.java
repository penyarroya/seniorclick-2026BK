package es.jlrn.configuration.auth.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequestDTO {
    private String code;          // <-- campo OTP necesario
    private String email;
    //private String password;
    private String token;        // Token recibido en el enlace
    private String newPassword;  // Nueva contraseña
}
