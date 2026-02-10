package es.jlrn.configuration.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.jlrn.configuration.auth.dtos.AuthResponseDTO;
import es.jlrn.configuration.auth.dtos.ForgotPasswordResponseDTO;
import es.jlrn.configuration.auth.dtos.LoginRequestDTO;
import es.jlrn.configuration.auth.dtos.ProfileUpdateDTO;
import es.jlrn.configuration.auth.dtos.RegisterRequestDTO;
import es.jlrn.configuration.jwt.JwtService;
import es.jlrn.exceptions.exception.*;
import es.jlrn.persistence.enums.VerificationStatus;
import es.jlrn.persistence.models.universilabs.models.UserProfile;
import es.jlrn.persistence.models.users.models.RefreshTokenEntity;
import es.jlrn.persistence.models.users.models.RoleEntity;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.repositories.RefreshTokenRepository;
import es.jlrn.persistence.models.users.repositories.RoleRepository;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.users.dtos.Usuarios.UserVerificationDTO;
import es.jlrn.presentation.users.services.impl.EmailServiceImpl;
import es.jlrn.presentation.users.services.impl.UserCleanupServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
//
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailServiceImpl emailService;
    private final JwtService jwtService;
    private final UserCleanupServiceImpl userCleanupService;
    private final IPasswordResetTokenService passwordResetTokenService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final int VERIFICATION_EXPIRATION_MINUTES = 3;
    private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000;

    private static final int REFRESH_GRACE_PERIOD_SECONDS = 15;
    

    @Value("${reset.jwt.expiration}")
    private int RESET_EXPIRATION_HOURS;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenDurationMs;

    @Value("${frontend.url}")
    private String frontendUrl;

    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{};:'\",.<>/?\\\\|`~]).{9,}$";

    public long getRefreshTokenDuration() {
        return refreshTokenDurationMs;
    }

    /**
     * Proporciona la URL configurada del frontend (leída desde application.properties).
     * Este era el método que faltaba y causaba el error de compilación
     * al ser llamado desde el controlador.
     * @return La URL base del frontend.
     */
    public String getFrontendUrl() {
        return frontendUrl;
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    //
    // public AuthResponseDTO login(LoginRequestDTO request) {
    //     try {
    //         Authentication authentication = authenticationManager.authenticate(
    //                 new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    //         );

    //         UserEntity user = (UserEntity) authentication.getPrincipal();
            
    //         // 1. Accedemos al perfil vinculado
    //         UserProfile profile = user.getProfile();

    //         String jwtToken = jwtService.generateToken(user);
    //         RefreshTokenEntity refreshToken = createRefreshToken(user);

    //         logger.info("Usuario con ID {} ha iniciado sesión correctamente (Identificador: {})",
    //                 user.getId(), request.getUsername());

    //         // 2. Mapeamos los campos de perfil al DTO de respuesta
    //         return AuthResponseDTO.builder()
    //                 .token(jwtToken)
    //                 .refreshToken(refreshToken.getToken())
    //                 .username(user.getUsername())
    //                 .email(user.getEmail())
    //                 .id(user.getId())
    //                 // --- NUEVOS CAMPOS ---
    //                 .firstName(profile != null ? profile.getFirstName() : "")
    //                 .lastName(profile != null ? profile.getLastName() : "")
    //                 // ---------------------
    //                 .tipo("Bearer")
    //                 .accessTokenDurationSec((int) (getAccessTokenDuration() / 1000))
    //                 .refreshTokenDurationSec((int) (getRefreshTokenDuration() / 1000))
    //                 .build();

    //     } catch (Exception e) {
    //         logger.error("Fallo de autenticación para el identificador {}", request.getUsername(), e);
    //         throw new InvalidCredentialsException("Credenciales incorrectas o usuario inactivo/no encontrado.");
    //     }
    // }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            // 1. AUTENTICACIÓN
            // El AuthenticationManager valida las credenciales. 
            // Si son incorrectas, lanza una excepción automáticamente (BadCredentialsException).
            // No asignamos el resultado a una variable para evitar el warning "unused variable".
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // 2. CARGA DE DATOS OPTIMIZADA
            // Recuperamos al usuario usando tu query personalizada con JOIN FETCH.
            // Esto carga User + Roles + Permissions + Profile en un único viaje a la DB.
            UserEntity user = userRepository.findByEmailOrUsernameWithRoles(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado tras la autenticación"));

            // 3. GENERACIÓN DE TOKENS
            // Al tener el usuario cargado con sus roles, jwtService no necesita hacer más consultas.
            String jwtToken = jwtService.generateToken(user);
            RefreshTokenEntity refreshToken = createRefreshToken(user);

            // 4. ACCESO AL PERFIL
            UserProfile profile = user.getProfile();

            logger.info("Sesión iniciada correctamente para el usuario: {} (ID: {})", 
                        user.getUsername(), user.getId());

            // 5. CONSTRUCCIÓN DE LA RESPUESTA (AuthResponseDTO)
            return AuthResponseDTO.builder()
                    .token(jwtToken)
                    .refreshToken(refreshToken.getToken())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .id(user.getId())
                    // Mapeo de campos del perfil (evitamos nulls con operador ternario)
                    .firstName(profile != null ? profile.getFirstName() : "")
                    .lastName(profile != null ? profile.getLastName() : "")
                    // Metadatos y duraciones (usando tus constantes y métodos de clase)
                    .tipo("Bearer")
                    .accessTokenDurationSec((int) (ACCESS_TOKEN_VALIDITY / 1000))
                    .refreshTokenDurationSec((int) (refreshTokenDurationMs / 1000))
                    .build();

        } catch (Exception e) {
            logger.error("Error de inicio de sesión para {}: {}", request.getUsername(), e.getMessage());
            // Lanzamos tu excepción personalizada para que el GlobalExceptionHandler la capture
            throw new InvalidCredentialsException("Credenciales inválidas, usuario inactivo o no encontrado.");
        }
    }

    //
    @Transactional
    public UserVerificationDTO register(RegisterRequestDTO request) {
        // 1. Validaciones de Contraseña y Duplicados
        validatePassword(request.getPassword());
        userCleanupService.cleanupBeforeCreate(request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException(request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException(request.getEmail());
        }

        // 2. Obtener el Rol por defecto
        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("USER"));

        // 3. Construir la entidad de Usuario
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .activo(false)
                .emailVerified(false)
                .roles(Set.of(userRole))
                .build();

        // 4. Construir el Perfil y VINCULARLO al usuario
        UserProfile profile = UserProfile.builder()
                .user(user) 
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();

        user.setProfile(profile);

        // 5. Configurar el código de verificación
        setupVerification(user);

        // 6. Guardar el usuario (Cascade guarda el perfil)
        userRepository.save(user);

        // 7. Envío de Email
        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getVerificationCode());
        } catch (Exception e) {
            logger.error("Error enviando email de verificación a {}", user.getEmail(), e);
            throw new EmailSendingException("No se pudo enviar email de verificación");
        }

        // 8. Preparar la respuesta para el Frontend (MAPEO DE CAMPOS NUEVOS)
        LocalDateTime expiryTime = user.getVerificationCodeExpiry();
        long expiryMillis = expiryTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        String expiryIso = expiryTime.toString();

        return UserVerificationDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // 🔹 ESTO ES LO QUE DEBES AÑADIR PARA QUE NO SALGAN NULL
                .firstName(user.getProfile().getFirstName())
                .lastName(user.getProfile().getLastName())
                // -------------------------------------------------------
                .verificationPending(true)
                .expiry(expiryMillis)
                .expiryIso(expiryIso)
                .build();
    }

    @Transactional
    public boolean deleteUnverifiedUser(String email) {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            if (!user.isEmailVerified()) {
                userRepository.delete(user);
                return true;
            }
        }
        return false;
    }

    // @Transactional
    // public UserVerificationDTO updateProfile(String username, ProfileUpdateDTO Bosch) {
    //     UserEntity user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

    //     UserProfile profile = user.getProfile();
        
    //     // Si por alguna razón no tuviera perfil, lo creamos (seguridad extra)
    //     if (profile == null) {
    //         profile = new UserProfile();
    //         profile.setUser(user);
    //         user.setProfile(profile);
    //     }

    //     // Actualizamos los campos
    //     profile.setFirstName(Bosch.getFirstName());
    //     profile.setLastName(Bosch.getLastName());
    //     profile.setPhone(Bosch.getPhone());

    //     userRepository.save(user); // La cascada guarda el perfil automáticamente

    //     return UserVerificationDTO.builder()
    //             .id(user.getId())
    //             .username(user.getUsername())
    //             .email(user.getEmail())
    //             .firstName(profile.getFirstName())
    //             .lastName(profile.getLastName())
    //             .message("Perfil actualizado correctamente")
    //             .status(VerificationStatus.SUCCESS)
    //             .build();
    // }

    @Transactional
    public UserVerificationDTO updateProfile(String username, ProfileUpdateDTO Bosch) {
        // CAMBIO: Usar la query optimizada en lugar de findByUsername
        UserEntity user = userRepository.findByEmailOrUsernameWithRoles(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        UserProfile profile = user.getProfile();
        
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        profile.setFirstName(Bosch.getFirstName());
        profile.setLastName(Bosch.getLastName());
        profile.setPhone(Bosch.getPhone());

        userRepository.save(user); 

        return UserVerificationDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .message("Perfil actualizado correctamente")
                .status(VerificationStatus.SUCCESS)
                .build();
    }

    // @Transactional
    // public UserVerificationDTO verifyEmail(String email, String code) {
    //     UserEntity user = userRepository.findByEmail(email).orElse(null);

    //     if (user == null) {
    //         return UserVerificationDTO.builder()
    //                 .message("Usuario no encontrado")
    //                 .status(VerificationStatus.NOT_FOUND)
    //                 .verificationPending(false)
    //                 .build();
    //     }

    //     LocalDateTime expiryTime = user.getVerificationCodeExpiry();
    //     Long expiryMillis = null;
    //     String expiryIso = null;

    //     if (expiryTime != null) {
    //         expiryMillis = expiryTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    //         expiryIso = expiryTime.toString();
    //     }

    //     if (expiryTime != null && expiryTime.isBefore(LocalDateTime.now())) {
    //         Long userId = user.getId();
    //         userRepository.delete(user);

    //         return UserVerificationDTO.builder()
    //                 .id(userId)
    //                 .deletedUserId(userId)
    //                 .message("Codigo expirado, usuario eliminado")
    //                 .status(VerificationStatus.EXPIRED)
    //                 .verificationPending(false)
    //                 .expiry(expiryMillis)
    //                 .expiryIso(expiryIso)
    //                 .build();
    //     }

    //     if (!code.equals(user.getVerificationCode())) {
    //         return UserVerificationDTO.builder()
    //                 .id(user.getId())
    //                 .message("Codigo incorrecto")
    //                 .status(VerificationStatus.INCORRECT)
    //                 .verificationPending(true)
    //                 .expiry(expiryMillis)
    //                 .expiryIso(expiryIso)
    //                 .build();
    //     }

    //     user.setActivo(true);
    //     user.setEmailVerified(true);
    //     user.setVerificationCode(null);
    //     user.setVerificationCodeExpiry(null);
    //     userRepository.save(user);

    //     return UserVerificationDTO.builder()
    //             .id(user.getId())
    //             .username(user.getUsername())
    //             .email(user.getEmail())
    //             .message("Codigo verificado correctamente")
    //             .status(VerificationStatus.SUCCESS)
    //             .verificationPending(false)
    //             .expiry(expiryMillis)
    //             .expiryIso(expiryIso)
    //             .build();
    // }

    @Transactional
    public UserVerificationDTO verifyEmail(String email, String code) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return UserVerificationDTO.builder()
                    .message("Usuario no encontrado")
                    .status(VerificationStatus.NOT_FOUND)
                    .verificationPending(false)
                    .build();
        }

        LocalDateTime expiryTime = user.getVerificationCodeExpiry();
        Long expiryMillis = null;
        String expiryIso = null;

        if (expiryTime != null) {
            expiryMillis = expiryTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            expiryIso = expiryTime.toString();
        }

        // Si el código expira, eliminamos al usuario. 
        // CascadeType.ALL borrará también el UserProfile automáticamente.
        if (expiryTime != null && expiryTime.isBefore(LocalDateTime.now())) {
            Long userId = user.getId();
            userRepository.delete(user);

            return UserVerificationDTO.builder()
                    .id(userId)
                    .deletedUserId(userId)
                    .message("Codigo expirado, usuario eliminado")
                    .status(VerificationStatus.EXPIRED)
                    .verificationPending(false)
                    .expiry(expiryMillis)
                    .expiryIso(expiryIso)
                    .build();
        }

        if (!code.equals(user.getVerificationCode())) {
            return UserVerificationDTO.builder()
                    .id(user.getId())
                    .message("Codigo incorrecto")
                    .status(VerificationStatus.INCORRECT)
                    .verificationPending(true)
                    .expiry(expiryMillis)
                    .expiryIso(expiryIso)
                    .build();
        }

        // ÉXITO: Activamos la cuenta
        user.setActivo(true);
        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);

        // Accedemos al perfil para devolver los datos al frontend
        UserProfile profile = user.getProfile();

        return UserVerificationDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // --- NUEVOS CAMPOS DEL PERFIL ---
                .firstName(profile != null ? profile.getFirstName() : "")
                .lastName(profile != null ? profile.getLastName() : "")
                // --------------------------------
                .message("Codigo verificado correctamente")
                .status(VerificationStatus.SUCCESS)
                .verificationPending(false)
                .expiry(expiryMillis)
                .expiryIso(expiryIso)
                .build();
    }

    private void validatePassword(String password) {
        if (!password.matches(PASSWORD_REGEX)) {
            throw new WeakPasswordException(
                    "La contraseña debe tener al menos 9 caracteres, una letra mayuscula, una minuscula, un numero y un caracter especial."
            );
        }
    }

    @Transactional
    public ForgotPasswordResponseDTO forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Generar código OTP de 6 dígitos y establecer expiración
        String code = generateVerificationCode();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(VERIFICATION_EXPIRATION_MINUTES);

        user.setPasswordResetCode(code);
        user.setPasswordResetCodeExpiry(expiry);
        userRepository.saveAndFlush(user);

        // Enviar correo con el código OTP
        emailService.sendResetEmail(email, code);

        return ForgotPasswordResponseDTO.builder()
                .success(true)
                .message("Se ha enviado un correo con el código para restablecer tu contraseña")
                .build();
    }

    // -------------------- RESET PASSWORD CON CÓDIGO OTP --------------------
    @Transactional
    public void resetPasswordWithCode(String email, String code, String newPassword) {
        validatePassword(newPassword);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // Validar código OTP y expiración
        if (user.getPasswordResetCode() == null
                || !user.getPasswordResetCode().equals(code)
                || user.getPasswordResetCodeExpiry() == null
                || user.getPasswordResetCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Código inválido o expirado.");
        }

        // Actualizar la contraseña y limpiar el código OTP
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetCode(null);
        user.setPasswordResetCodeExpiry(null);
        userRepository.saveAndFlush(user);
    }

    // -------------------- MÉTODO AUXILIAR: GENERAR CÓDIGO OTP --------------------
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000; // 6 dígitos
        return String.valueOf(code);
    }

    @Transactional
    public void resetPasswordByToken(String token, String newPassword) {
        validatePassword(newPassword);

        String email = passwordResetTokenService.getEmailFromResetToken(token);
        if (email == null) {
            throw new InvalidTokenException("El enlace de restablecimiento es invalido o ha caducado.");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Token valido, pero el usuario no fue encontrado."));

        if (user.getPasswordResetToken() == null || !token.equals(user.getPasswordResetToken())) {
            throw new InvalidTokenException("Token de seguridad no valido o expirado.");
        }

        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            userRepository.saveAndFlush(user);
            throw new InvalidTokenException("El token ha expirado. Solicita un nuevo enlace.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setPasswordResetConfirmed(false);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public String confirmResetByToken(String token) {
        String email = passwordResetTokenService.getEmailFromResetToken(token);
        if (email == null) {
            throw new InvalidTokenException("El enlace de restablecimiento es invalido o ha caducado.");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + email));

        if (user.getPasswordResetToken() == null || !token.equals(user.getPasswordResetToken())) {
            throw new InvalidTokenException("Token de seguridad no valido o no coincide.");
        }

        if (user.getPasswordResetTokenExpiry() == null ||
                user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {

            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            user.setPasswordResetConfirmed(false);
            userRepository.saveAndFlush(user);
            throw new InvalidTokenException("El token ha expirado. Solicita un nuevo enlace.");
        }

        user.setPasswordResetConfirmed(true);
        userRepository.saveAndFlush(user);
        logger.info("Confirmacion de reseteo exitosa para el email: {}", email);

        return email;
    }

    @Transactional
    public void confirmResetRequest(String email, String token) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (user.getPasswordResetToken() == null ||
                !user.getPasswordResetToken().equals(token) ||
                user.getPasswordResetTokenExpiry() == null ||
                user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("El enlace ha expirado o no es valido.");
        }

        user.setPasswordResetConfirmed(true);
        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void resetPasswordByEmail(String email, String newPassword) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if (user.getPasswordResetToken() == null ||
                user.getPasswordResetTokenExpiry() == null ||
                user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now()) ||
                !user.getPasswordResetConfirmed()) {
            throw new InvalidTokenException("No se puede restablecer la contraseña. Solicita nuevamente el cambio.");
        }

        validatePassword(newPassword);

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setPasswordResetConfirmed(false);

        userRepository.saveAndFlush(user);
    }

    // @Transactional
    // public RefreshTokenEntity createRefreshToken(UserEntity user) {
    //     long refreshTokenDurationMs = getRefreshTokenDuration();

    //     RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
    //             .user(user)
    //             .token(generateRandomToken())
    //             .createdAt(LocalDateTime.now())
    //             .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000))
    //             .revoked(false)
    //             .expired(false)
    //             .build();

    //     return refreshTokenRepository.save(refreshToken);
    // }

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        long refreshTokenDurationMs = getRefreshTokenDuration();

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(generateRandomToken())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenDurationMs / 1000))
                .revoked(false)
                .revokedAt(null) // Aseguramos que empiece limpio
                .expired(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public boolean isPasswordResetConfirmed(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
        return user.getPasswordResetConfirmed() != null && user.getPasswordResetConfirmed();
    }

    @Transactional
    public RefreshTokenEntity verifyRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token no valido"));
        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expirado o revocado");
        }
        return refreshToken;
    }

    // @Transactional
    // public AuthResponseDTO refreshAccessToken(String refreshTokenStr) {
    //     RefreshTokenEntity refreshToken = verifyRefreshToken(refreshTokenStr);
    //     UserEntity user = refreshToken.getUser();

    //     refreshToken.setRevoked(true);
    //     refreshTokenRepository.save(refreshToken);

    //     String newJwt = jwtService.generateToken(user);
    //     RefreshTokenEntity newRefreshToken = createRefreshToken(user);

    //     return AuthResponseDTO.builder()
    //             .token(newJwt)
    //             .refreshToken(newRefreshToken.getToken())
    //             .username(user.getUsername())
    //             .email(user.getEmail())
    //             .id(user.getId())
    //             .tipo("Bearer")
    //             .accessTokenDurationSec((int) (getAccessTokenDuration() / 1000))
    //             .refreshTokenDurationSec((int) (getRefreshTokenDuration() / 1000))
    //             .build();
    // }


    @Transactional
    public AuthResponseDTO refreshAccessToken(String refreshTokenStr) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new InvalidTokenException("Refresh token no encontrado"));

        // --- LÓGICA DE PERÍODO DE GRACIA ---
        if (refreshToken.isRevoked()) {
            LocalDateTime now = LocalDateTime.now();
            if (refreshToken.getRevokedAt() != null && 
                refreshToken.getRevokedAt().plusSeconds(REFRESH_GRACE_PERIOD_SECONDS).isAfter(now)) {
                
                logger.info("Petición paralela detectada dentro del período de gracia: {}", refreshTokenStr);
                throw new TokenAlreadyRefreshedException("El token se acaba de renovar en otra petición paralela.");
            } else {
                throw new InvalidTokenException("Refresh token ya utilizado y período de gracia expirado");
            }
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Refresh token expirado");
        }

        UserEntity user = refreshToken.getUser();
        
        // --- NUEVO: CARGAR EL PERFIL PARA EL DTO ---
        UserProfile profile = user.getProfile(); 

        // Marcamos como revocado
        refreshToken.setRevoked(true);
        refreshToken.setRevokedAt(LocalDateTime.now()); 
        refreshTokenRepository.save(refreshToken);

        // Generación de nuevos tokens
        String newJwt = jwtService.generateToken(user); 
        RefreshTokenEntity newRefreshToken = createRefreshToken(user);

        return AuthResponseDTO.builder()
                .token(newJwt)
                .refreshToken(newRefreshToken.getToken())
                .username(user.getUsername())
                .email(user.getEmail())
                .id(user.getId())
                // --- NUEVOS CAMPOS ASIGNADOS AQUÍ ---
                .firstName(profile != null ? profile.getFirstName() : "")
                .lastName(profile != null ? profile.getLastName() : "")
                // ------------------------------------
                .tipo("Bearer")
                .accessTokenDurationSec((int) (ACCESS_TOKEN_VALIDITY / 1000))
                .refreshTokenDurationSec((int) (refreshTokenDurationMs / 1000))
                .build();
    }

    // @Transactional
    // public void logout(String refreshTokenStr) {
    //     // 1. Buscamos el token pero NO lanzamos excepción si no existe
    //     refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(refreshToken -> {
    //         // 2. Solo si existe, lo revocamos
    //         refreshToken.setRevoked(true);
    //         refreshTokenRepository.save(refreshToken);
    //         logger.info("Refresh token revocado para el usuario: {}", refreshToken.getUser().getUsername());
    //     });
        
    //     // Si no existe, no hacemos nada y dejamos que el flujo continúe
    //     // Esto evita el Error 500 y permite que se borren las cookies HttpOnly
    // }


    @Transactional
    public void logout(String refreshTokenStr) {
        // 1. Buscamos el token de forma segura
        refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(refreshToken -> {
            UserEntity user = refreshToken.getUser();

            // 2. Marcamos el token actual como revocado con su marca de tiempo
            refreshToken.setRevoked(true);
            refreshToken.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(refreshToken);

            // 3. OPCIONAL: Por seguridad extrema, invalidamos TODOS sus tokens activos
            // Esto evita que si el usuario tenía sesiones abiertas en otros navegadores, 
            // estas también se cierren (útil para "Cerrar todas las sesiones").
            // refreshTokenRepository.revokeAllByUserId(user.getId(), LocalDateTime.now());

            logger.info("Sesión cerrada y token revocado para el usuario: {} (ID: {})", 
                        user.getUsername(), user.getId());
        });
        
        // Si no se encuentra el token, el método termina en silencio. 
        // Esto es correcto para evitar que el cliente reciba un error si intenta 
        // desloguearse dos veces seguidas.
    }

    //
    private void setupVerification(UserEntity user) {
        String code = generateVerificationCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(VERIFICATION_EXPIRATION_MINUTES));
    }

    private String generateRandomToken() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    public long getAccessTokenDuration() {
        return ACCESS_TOKEN_VALIDITY;
    }

    public RefreshTokenEntity getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token).orElse(null);
    }

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    public void cleanupExpiredAndRevokedRefreshTokens() {
        LocalDateTime now = LocalDateTime.now();
        // int deleted = refreshTokenRepository.deleteByExpiresAtBeforeOrRevokedTrue(now);
        int deleted = refreshTokenRepository.deleteByExpiresAtBeforeOrRevokedAtIsNotNull(now);
        if (deleted > 0) {
            logger.info("Refresh tokens expirados o revocados eliminados: {}", deleted);
        }
    }

    // cancela fogot-password
    @Transactional
    public boolean cancelForgotPassword(String email) {
        int updated = userRepository.clearForgotPasswordByEmail(email);
        return updated > 0;
    }

    //
    // public UserEntity getCurrentUser(String username) {
    //     return userRepository.findByUsername(username)
    //             .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
    // }

    public UserEntity getCurrentUser(String username) {
        // Esta es la clave para que el Controller no falle al acceder a roles o perfil
        return userRepository.findByEmailOrUsernameWithRoles(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));
    }
}