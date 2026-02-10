package es.jlrn.configuration.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements IPasswordResetTokenService {
//
    // NOTA IMPORTANTE: Debes configurar estas propiedades en tu application.properties/yml
    // Por ejemplo:
    // reset.jwt.secret=TU_SECRETO_BASE64_DE_AL_MENOS_32_BYTES_PARA_RESET
    // reset.jwt.expiration=1
    
    // Secreto para firmar los tokens de reseteo
    @Value("${reset.jwt.secret}")
    private String RESET_SECRET_KEY;

    // Tiempo de expiración del token de reseteo en horas (e.g., 1 hora)
    @Value("${reset.jwt.expiration}")
    private int RESET_EXPIRATION_HOURS; 

    @Override
    public String createResetToken(String email) {
        // En el token de reseteo, el subject es el email.
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                // Calcula la expiración en milisegundos
                .expiration(new Date(System.currentTimeMillis() + (long) RESET_EXPIRATION_HOURS * 3600000))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public String getEmailFromResetToken(String token) {
        try {
            // Reutilizamos la lógica de claims para obtener el subject (email)
            return getClaim(token, Claims::getSubject);
        } catch (Exception e) {
            // Captura de excepciones de JWT (token expirado, firma inválida, etc.)
            // En caso de error, retorna null para indicar que el token no es válido.
            return null;
        }
    }

    // --- Métodos Auxiliares para JWT ---
    
    private SecretKey getSignInKey() {
        // Decodifica el secreto BASE64
        byte[] keyBytes = Decoders.BASE64.decode(RESET_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims getAllClaims(String token) {
        // Parsea y verifica el token
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }
}