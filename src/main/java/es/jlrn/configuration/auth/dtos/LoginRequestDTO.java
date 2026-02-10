package es.jlrn.configuration.auth.dtos;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {
//
    // Usamos 'username' como identificador unificado (email o username)
    @NotBlank(message = "El nombre de usuario/email es obligatorio")
    private String username; 
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    // email puede ser null si se usa username
    //private String email;
    
}
