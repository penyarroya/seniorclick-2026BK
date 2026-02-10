package es.jlrn.presentation.users.dtos.Usuarios;

import es.jlrn.persistence.enums.VerificationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserVerificationDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName; // --- NUEVOS CAMPOS DEL PERFIL --
    private String lastName;
    private boolean verificationPending;
    private Long expiry;      // timestamp en ms
    private String expiryIso; // fecha legible
    private String message;
    private Long deletedUserId;
    private VerificationStatus status;  // Nuevo campo
}
