package es.jlrn.configuration.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
//
    // ⏰ Almacena tokens temporales: token -> info(email + expiración)
    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private static final int TOKEN_EXPIRATION_MINUTES = 15; // token válido 15 min

    // Genera un token para un email
    public String createResetToken(String email) {
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
        tokens.put(token, new TokenInfo(email, expiresAt));

        return token;
    }

    // Valida token y devuelve el email asociado, o null si inválido
    public String validateToken(String token) {
        TokenInfo info = tokens.get(token);
        if (info == null) return null;

        if (LocalDateTime.now().isAfter(info.expiresAt)) {
            tokens.remove(token);
            return null;
        }

        // Una vez usado, eliminar el token
        tokens.remove(token);
        return info.email;
    }

    // Clase interna para almacenar email + expiración
    private static class TokenInfo {
        String email;
        LocalDateTime expiresAt;

        TokenInfo(String email, LocalDateTime expiresAt) {
            this.email = email;
            this.expiresAt = expiresAt;
        }
    }
}

