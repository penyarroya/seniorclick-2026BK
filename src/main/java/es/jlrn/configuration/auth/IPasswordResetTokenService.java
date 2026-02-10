package es.jlrn.configuration.auth;

/**
 * Interfaz dedicada a la gestión de tokens de reseteo de contraseña.
 * Esto separa la lógica del token JWT de sesión (gestionado por JwtService)
 * de la lógica de tokens de uso único (Reset Token).
 */
public interface IPasswordResetTokenService {

    /**
     * Crea un token de reseteo basado en el email del usuario.
     * @param email El email del usuario.
     * @return El token generado (String).
     */
    String createResetToken(String email);

    /**
     * Valida el token de reseteo (firma, caducidad) y extrae el email (subject).
     * @param token El token JWT de reseteo.
     * @return El email del usuario si el token es válido; null o lanza excepción si es inválido/expirado.
     */
    String getEmailFromResetToken(String token);
}