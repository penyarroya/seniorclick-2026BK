package es.jlrn.configuration.auth;

import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import es.jlrn.configuration.auth.dtos.*;
import es.jlrn.exceptions.exception.InvalidTokenException;
import es.jlrn.exceptions.exception.UserNotFoundException;
import es.jlrn.presentation.users.dtos.Usuarios.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
//
    private final AuthService authService;
    private static final String REDIRECT_PATH = "/forgot-password";

    // -------------------- HEALTH CHECK --------------------
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "Backend OK"));
    }

    // -------------------- SESSION --------------------
    @GetMapping("/session")
    public ResponseEntity<Map<String, Boolean>> checkSession(Authentication authentication) {
        boolean authenticated = authentication != null && authentication.isAuthenticated();
        return ResponseEntity.ok(Map.of("authenticated", authenticated));
    }

    // -------------------- CHECK USER BY EMAIL --------------------
    @PostMapping("/check-user")
    public ResponseEntity<CheckUserResponseDTO> checkUser(@Valid @RequestBody CheckUserRequestDTO request) {
        boolean exists = authService.existsByEmail(request.getEmail());
        return ResponseEntity.ok(new CheckUserResponseDTO(exists));
    }

    // -------------------- LOGIN --------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO authResponse = authService.login(request);

        ResponseCookie accessCookie = createCookie(
                "accessToken",
                authResponse.getToken(),
                authResponse.getAccessTokenDurationSec(),
                "/",
                true,
                "Lax"
        );

        ResponseCookie refreshCookie = createCookie(
                "refreshToken",
                authResponse.getRefreshToken(),
                authResponse.getRefreshTokenDurationSec(),
                "/",
                true,
                "Lax"
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authResponse);
    }

    // -------------------- REGISTER --------------------
    @PostMapping("/register")
    public ResponseEntity<UserVerificationDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        UserVerificationDTO userDTO = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

    // -------------------- VERIFY EMAIL --------------------
    @PostMapping("/verify-email")
    public ResponseEntity<UserVerificationDTO> verifyEmail(
            @RequestParam String email,
            @RequestParam String code
    ) {
        UserVerificationDTO result = authService.verifyEmail(email, code);

        switch (result.getStatus()) {
            case SUCCESS:
                return ResponseEntity.ok(result);
            case PENDING:
            case INCORRECT:
            case EXPIRED:
                return ResponseEntity.badRequest().body(result);
            case NOT_FOUND:
            default:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    // -------------------- UPDATE PROFILER --------------------
    @PutMapping("/profile")
    public ResponseEntity<UserVerificationDTO> updateProfile(
            @Valid @RequestBody ProfileUpdateDTO request,
            Authentication authentication) {
        
        // Obtenemos el nombre de usuario directamente del SecurityContext (JWT)
        UserVerificationDTO updatedUser = authService.updateProfile(authentication.getName(), request);
        return ResponseEntity.ok(updatedUser);
    }

    // -------------------- FORGOT PASSWORD --------------------
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        ForgotPasswordResponseDTO response = authService.forgotPassword(email);
        return ResponseEntity.ok(response);
    }
    
    // -------------------- RESET LINK CONFIRMATION --------------------
    @GetMapping("/reset-link-confirmation")
    public ResponseEntity<Void> confirmResetLink(@RequestParam("token") String token) {
        String redirectUrl;

        try {
            String email = authService.confirmResetByToken(token);

            redirectUrl = authService.getFrontendUrl() + REDIRECT_PATH +
                    "?token=" + token + "&status=confirmed&email=" + email;

        } catch (InvalidTokenException e) {
            redirectUrl = authService.getFrontendUrl() + REDIRECT_PATH +
                    "?status=error&message=" + e.getMessage();
        } catch (UserNotFoundException e) {
            redirectUrl = authService.getFrontendUrl() + REDIRECT_PATH +
                    "?status=error&message=El usuario asociado al token no existe o el enlace es invalido.";
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }

    // -------------------- RESET PASSWORD (FINAL POST) --------------------
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        try {
            authService.resetPasswordWithCode(request.getEmail(), request.getCode(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña restablecida correctamente"));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    // -------------------- DELETE TEMPORARY USER --------------------
    @DeleteMapping("/temp-user")
    public ResponseEntity<Map<String, String>> deleteUnverifiedUser(@RequestParam String email) {
        boolean deleted = authService.deleteUnverifiedUser(email);

        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Usuario no verificado eliminado"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "No se encontro usuario sin verificar con ese email o ya estaba verificado."));
        }
    }

    // -------------------- REFRESH TOKEN --------------------
    // @PostMapping("/refresh-token")
    // public ResponseEntity<AuthResponseDTO> refreshToken(
    //         @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie,
    //         @RequestParam(required = false) String refreshTokenParam
    // ) {

    //     String tokenToUse = refreshTokenCookie != null ? refreshTokenCookie : refreshTokenParam;

    //     if (tokenToUse == null) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    //     }

    //     var refreshToken = authService.getRefreshToken(tokenToUse);

    //     if (refreshToken == null || refreshToken.isRevoked() || refreshToken.isExpired()) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    //     }

    //     AuthResponseDTO newTokens = authService.refreshAccessToken(tokenToUse);

    //     // --- CAMBIO AQUÍ: Usamos "/" en lugar de "/auth/refresh-token" ---
    //     ResponseCookie refreshCookie = createCookie(
    //             "refreshToken",
    //             newTokens.getRefreshToken(),
    //             newTokens.getRefreshTokenDurationSec(),
    //             "/", 
    //             true,
    //             "Lax"
    //     );

    //     ResponseCookie accessCookie = createCookie(
    //             "accessToken",
    //             newTokens.getToken(),
    //             newTokens.getAccessTokenDurationSec(),
    //             "/",
    //             true,
    //             "Lax"
    //     );

    //     return ResponseEntity.ok()
    //             .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
    //             .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
    //             .body(newTokens);
    // }


    // -------------------- REFRESH TOKEN --------------------
    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshTokenCookie,
            @RequestParam(required = false) String refreshTokenParam
    ) {
        String tokenToUse = refreshTokenCookie != null ? refreshTokenCookie : refreshTokenParam;

        if (tokenToUse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Eliminamos la verificación manual de .isRevoked() aquí porque 
        // ahora esa lógica vive dentro de authService.refreshAccessToken()
        // y es la que lanza el 409 si detecta el "periodo de gracia".
        
        AuthResponseDTO newTokens = authService.refreshAccessToken(tokenToUse);

        // Si llegamos aquí, el refresh fue exitoso
        ResponseCookie refreshCookie = createCookie(
                "refreshToken",
                newTokens.getRefreshToken(),
                newTokens.getRefreshTokenDurationSec(),
                "/", 
                true,
                "Lax"
        );

        ResponseCookie accessCookie = createCookie(
                "accessToken",
                newTokens.getToken(),
                newTokens.getAccessTokenDurationSec(),
                "/",
                true,
                "Lax"
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(newTokens);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        
        if (refreshToken != null && !refreshToken.isEmpty()) {
            try {
                authService.logout(refreshToken);
            } catch (Exception e) {
                log.warn("No se pudo revocar el token en DB, procediendo a borrar cookies");
            }
        }

        // Solo necesitamos borrar las cookies en la raíz "/" 
        // porque ya unificamos todos los paths en login y refresh.
        ResponseCookie clearAccess = createCookie("accessToken", "", 0, "/", true, "Lax");
        ResponseCookie clearRefresh = createCookie("refreshToken", "", 0, "/", true, "Lax");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccess.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefresh.toString())
                .body(Map.of("message", "Sesión cerrada correctamente"));
    }

    // -------------------- CANCELAR FORGOT-PASSWORD --------------------
    @PostMapping("/forgot-password/cancel")
    public ResponseEntity<Map<String, String>> cancelForgotPassword(@RequestBody CancelForgotPasswordRequest request) {
        boolean success = authService.cancelForgotPassword(request.getEmail());

        if (success) {
            return ResponseEntity.ok(Map.of("message", "Código de restablecimiento eliminado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(Map.of("message", "No se encontró usuario con ese email o no había código activo"));
        }
    }
    
    // @GetMapping("/me")
    // public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
    //     if (authentication == null || !authentication.isAuthenticated()) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }

    //     var user = authService.getCurrentUser(authentication.getName());
    //     var profile = user.getProfile(); 

    //     // Retornamos un mapa plano que el frontend pueda consumir fácilmente
    //     return ResponseEntity.ok(Map.of(
    //             "userId", user.getId(),
    //             "username", user.getUsername(),
    //             "email", user.getEmail(),
    //             "firstName", profile != null ? profile.getFirstName() : "",
    //             "lastName", profile != null ? profile.getLastName() : "",
    //             "avatarUrl", (profile != null && profile.getAvatarUrl() != null) ? profile.getAvatarUrl() : "",
    //             "roles", user.getRoles().stream().map(r -> r.getName()).toList()
    //     ));
    // }

   @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Llamamos al service, NO al repository directamente
        var user = authService.getCurrentUser(authentication.getName());
        var profile = user.getProfile(); 

        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "firstName", profile != null ? profile.getFirstName() : "",
                "lastName", profile != null ? profile.getLastName() : "",
                "avatarUrl", (profile != null && profile.getAvatarUrl() != null) ? profile.getAvatarUrl() : "",
                "roles", user.getRoles().stream().map(r -> r.getName()).toList()
        ));
    }

    // -------------------- COOKIE CREATOR --------------------
    private ResponseCookie createCookie(
            String name,
            String value,
            long maxAgeSec,
            String path,
            boolean httpOnly,
            String sameSite
    ) {
        boolean isProd = System.getenv("ENV") != null && System.getenv("ENV").equals("PROD");

        return ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(isProd)
                .path(path)
                .sameSite(sameSite)
                .maxAge(maxAgeSec)
                .build();
    }
}

