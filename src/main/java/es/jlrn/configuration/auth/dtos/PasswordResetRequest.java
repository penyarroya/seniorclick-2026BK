package es.jlrn.configuration.auth.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para manejar la solicitud de restablecimiento de contraseña.
 * Contiene el token recibido por correo y la nueva contraseña.
 */
public class PasswordResetRequest {

    @NotBlank(message = "El token es obligatorio.")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String newPassword;

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}