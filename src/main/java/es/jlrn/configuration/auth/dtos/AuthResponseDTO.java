package es.jlrn.configuration.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
//
    private String token;
    private String refreshToken; // <- agregar este campo
    private String username;
    private String email;
    private Long id;
    private String firstName; // --- NUEVOS CAMPOS DEL PERFIL ---
    private String lastName;
    @Builder.Default
    private String tipo = "Bearer";
    // Duraciones en segundos para cookies
    private int accessTokenDurationSec;
    private int refreshTokenDurationSec;
    
}
