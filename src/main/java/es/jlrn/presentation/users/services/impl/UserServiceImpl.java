package es.jlrn.presentation.users.services.impl;

import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.models.RoleEntity;
import es.jlrn.persistence.models.users.repositories.RoleRepository;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.configuration.auth.dtos.RegisterRequestDTO;
import es.jlrn.exceptions.exception.DuplicateUserException;
import es.jlrn.exceptions.exception.RoleNotFoundException;
import es.jlrn.persistence.enums.VerificationStatus;
import es.jlrn.presentation.users.dtos.Usuarios.*;
import es.jlrn.presentation.users.mappers.UserMapper;
import es.jlrn.presentation.users.services.interfaces.IEmailService;
import es.jlrn.presentation.users.services.interfaces.IUserCleanupService;
import es.jlrn.presentation.users.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import es.jlrn.persistence.models.universilabs.models.UserProfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
//
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final int VERIFICATION_EXPIRATION_MINUTES = 3;

    private final UserRepository userRepository;
    private final IUserCleanupService userCleanupService;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    

    // public UserServiceImpl(UserRepository userRepository,
    //                        IUserCleanupService userCleanupService,
    //                        PasswordEncoder passwordEncoder,
    //                        IEmailService emailService,
    //                        RoleRepository roleRepository,UserMapper userMapper) {
    //     this.userRepository = userRepository;
    //     this.userCleanupService = userCleanupService;
    //     this.passwordEncoder = passwordEncoder;
    //     this.emailService = emailService;
    //     this.roleRepository = roleRepository;
    //     this.userMapper = userMapper;
    // }

    @Transactional
    public UserVerificationDTO register(RegisterRequestDTO request) {
        // 1. Validaciones iniciales (Mantenemos tu lógica actual)
        validatePassword(request.getPassword());
        userCleanupService.cleanupBeforeCreate(request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUserException(request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException(request.getEmail());
        }

        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("USER"));

        // 2. Crear la entidad de Usuario (Base)
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .activo(false)
                .emailVerified(false)
                .roles(Set.of(userRole))
                .build();

        // 3. NUEVO: Crear el perfil y vincularlo
        // Es vital pasarle el objeto 'user' para que @MapsId funcione correctamente
        UserProfile profile = UserProfile.builder()
                .user(user) 
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone()) // Si añadiste teléfono al DTO
                .build();

        // Establecer la relación bidireccional
        user.setProfile(profile);

        // 4. Lógica de verificación (Mantenemos tu código)
        setupVerification(user);

        // 5. Persistencia
        // Al guardar 'user', JPA guardará automáticamente el 'profile' 
        // gracias a CascadeType.ALL que configuramos en la entidad UserEntity
        userRepository.save(user);

        // 6. Procesos Post-guardado (Mantenemos tu código)
        sendVerificationEmailAsync(user.getEmail(), user.getVerificationCode());
        userCleanupService.scheduleDeleteIfNotVerified(user.getId(), VERIFICATION_EXPIRATION_MINUTES);

        return buildUserVerificationDTO(user);
    }

    // ====================== VERIFICACIÓN DE EMAIL ======================
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

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = user.getVerificationCodeExpiry();
        Long expiryMillis = expiryTime != null ? expiryTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null;
        String expiryIso = expiryTime != null ? expiryTime.toString() : null;

        if (expiryTime != null && expiryTime.isBefore(now)) {
            Long userId = user.getId();
            userRepository.delete(user);
            return UserVerificationDTO.builder()
                    .id(userId)
                    .deletedUserId(userId)
                    .message("⏰ Código expirado, usuario eliminado")
                    .status(VerificationStatus.EXPIRED)
                    .verificationPending(false)
                    .expiry(expiryMillis)
                    .expiryIso(expiryIso)
                    .build();
        }

        if (!code.equals(user.getVerificationCode())) {
            return UserVerificationDTO.builder()
                    .id(user.getId())
                    .message("❌ Código incorrecto")
                    .status(VerificationStatus.INCORRECT)
                    .verificationPending(true)
                    .expiry(expiryMillis)
                    .expiryIso(expiryIso)
                    .build();
        }

        // Código correcto
        user.setActivo(true);
        user.setEmailVerified(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiry(null);
        userRepository.save(user);
        userCleanupService.cancelScheduledDelete(user.getId());

        return buildUserVerificationDTO(user);
    }

    // ====================== MÉTODOS INTERNOS ======================
    private void setupVerification(UserEntity user) {
        String code = generateVerificationCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(VERIFICATION_EXPIRATION_MINUTES));
    }

    private String generateVerificationCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    // Construye el DTO de verificación del usuario
    private UserVerificationDTO buildUserVerificationDTO(UserEntity user) {
        LocalDateTime expiryTime = user.getVerificationCodeExpiry();
        long expiryMillis = expiryTime != null ? expiryTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : 0;
        String expiryIso = expiryTime != null ? expiryTime.toString() : null;

        // Extraemos el perfil de forma segura para evitar NullPointerException
        var profile = user.getProfile();

        return UserVerificationDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                // --- NUEVOS CAMPOS ---
                .firstName(profile != null ? profile.getFirstName() : "")
                .lastName(profile != null ? profile.getLastName() : "")
                // ---------------------
                .verificationPending(!user.isEmailVerified())
                .expiry(expiryMillis)
                .expiryIso(expiryIso)
                .message(user.isEmailVerified() ? "✅ Verificado" : "⏳ Pendiente de verificación")
                .status(user.isEmailVerified() ? VerificationStatus.SUCCESS : VerificationStatus.PENDING)
                .build();
    }

    @Async
    public void sendVerificationEmailAsync(String email, String code) {
        try {
            emailService.sendVerificationEmail(email, code);
            logger.info("Email de verificación enviado a {}", email);
        } catch (Exception e) {
            logger.error("Error enviando email de verificación a {}", email, e);
        }
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // cada 5 minutos
    @Transactional
    public void limpiarUsuariosNoVerificadosScheduler() {
        LocalDateTime now = LocalDateTime.now();
        List<UserEntity> expiredUsers = userRepository.findExpiredUnverifiedUsers(now);
        if (!expiredUsers.isEmpty()) {
            userRepository.deleteAll(expiredUsers);
            logger.info("Usuarios no verificados eliminados: {}", expiredUsers.size());
        }
    }

    private void validatePasswordInternal(String password) {
    //    
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        if (password.length() < 9) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 9 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra minúscula");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un dígito");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-={}\\[\\]:;\"'|,<.>/?`~].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un carácter especial");
        }

        if (password.contains(" ")) {
            throw new IllegalArgumentException("La contraseña no debe contener espacios");
        }
    }

    @Override
    public void validatePassword(String password) {
        validatePasswordInternal(password);
    }

    // ====================== CRUD y otros métodos ======================
    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    //
    @Override
    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        // 1. Validar contraseña
        validatePassword(dto.getPassword());

        // 2. Limpieza y validación de duplicados
        userCleanupService.cleanupBeforeCreate(dto.getEmail());
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUserException(dto.getUsername());
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException(dto.getEmail());
        }

        // 3. PROCESAR ROLES DINÁMICAMENTE 
        Set<RoleEntity> rolesEntities = new HashSet<>();
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            rolesEntities = dto.getRoles().stream()
                    .map(roleEnum -> roleRepository.findByName(roleEnum.name())
                            .orElseThrow(() -> new RoleNotFoundException(roleEnum.name())))
                    .collect(Collectors.toSet());
        } else {
            RoleEntity userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RoleNotFoundException("USER"));
            rolesEntities.add(userRole);
        }

        // 4. Crear la entidad UserEntity
        UserEntity user = UserEntity.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .emailVerified(true) // En creación manual suele marcarse como verificado
                .roles(rolesEntities)
                .build();

        // 5. NUEVO: Inicializar el Perfil
        // Si tu UserCreateDTO tiene firstName/lastName úsalos, 
        // si no, ponemos valores por defecto para evitar errores de BD.
        UserProfile profile = UserProfile.builder()
                .user(user)
                .firstName(dto.getFirstName() != null ? dto.getFirstName() : "Usuario")
                .lastName(dto.getLastName() != null ? dto.getLastName() : "Nuevo")
                .build();

        // Establecemos la relación bidireccional
        user.setProfile(profile);

        // 6. Guardar (CascadeType.ALL se encarga de guardar el perfil)
        userRepository.save(user);

        return toDTO(user);
    }

    //
    @Override
    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        // 1. Buscar el usuario
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + id));

        // 2. Usar el Mapper para actualizar campos básicos (username, email, activo, firstName, lastName, phone)
        // Esto sustituye todos tus "if" manuales de cuenta y perfil
        userMapper.updateEntityFromDTO(dto, user);

        // 3. Lógica especial: Contraseña (requiere encriptación)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 4. Lógica de negocio: Roles (requiere consulta al repositorio)
        if (dto.getRoles() != null) {
            if (dto.getRoles().isEmpty()) {
                throw new IllegalArgumentException("El usuario debe tener al menos un rol");
            }
            Set<RoleEntity> newRoles = dto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RoleNotFoundException(roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
        }

        // 5. Persistencia y retorno
        // Al guardar 'user', el perfil se actualiza automáticamente por el CascadeType.ALL
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + id));
        userRepository.delete(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    // @Override
    // public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
    //     if (input == null || input.isBlank()) {
    //         throw new UsernameNotFoundException("El username o email no puede estar vacío");
    //     }

    //     String normalizedInput = input.trim();

    //     UserEntity user = userRepository.findByEmailIgnoreCaseAndActivoTrue(normalizedInput)
    //             .or(() -> userRepository.findByUsernameIgnoreCaseAndActivoTrue(normalizedInput))
    //             .orElseThrow(() -> new UsernameNotFoundException(
    //                     "Usuario no encontrado o inactivo: " + normalizedInput));

    //     // Aquí puedes añadir logging si quieres
    //     logger.info("Usuario autenticado: {}", user.getUsername());

    //     return user;
    // }

    @Override
    @Transactional(readOnly = true) // <--- Añade esto
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        if (input == null || input.isBlank()) {
            throw new UsernameNotFoundException("El username o email no puede estar vacío");
        }

        // Buscamos con la query optimizada que trae Roles y Permisos de un golpe
        UserEntity user = userRepository.findByEmailOrUsernameWithRoles(input.trim())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con: " + input));

        if (!user.isActivo()) {
            logger.warn("Intento de login para usuario inactivo: {}", input);
            throw new UsernameNotFoundException("El usuario está inactivo");
        }

        logger.info("Usuario cargado con éxito desde DB (Single Query): {}", user.getUsername());
        return user; 
    }

    @Override
    @Transactional
    public UserDTO toggleActivo(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setActivo(!user.isActivo());
        userRepository.save(user);

        logger.info("Usuario con id {} ahora activo={}", id, user.isActivo());
        return toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO cambiarEstadoActivo(Long userId, boolean activo) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (user.isActivo() != activo) {
            user.setActivo(activo);
            userRepository.save(user);
            logger.info("Usuario con id {} cambiado activo={}", userId, activo);
        }

        return toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO addRoleToUser(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + userId));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));

        if (user.getRoles().contains(role)) {
            throw new IllegalArgumentException("El usuario ya tiene el rol: " + roleName);
        }

        user.getRoles().add(role);
        userRepository.save(user);

        logger.info("Rol '{}' añadido al usuario '{}'", roleName, user.getUsername());
        return toDTO(user);
    }
    
    @Override
    @Transactional
    public UserDTO removeRoleFromUser(Long userId, String roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con id: " + userId));

        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));

        if (!user.getRoles().contains(role)) {
            throw new IllegalArgumentException("El usuario no tiene el rol: " + roleName);
        }

        // Verificar que quede al menos un rol
        if (user.getRoles().size() <= 1) {
            throw new IllegalStateException("El usuario debe tener al menos un rol");
        }

        user.getRoles().remove(role);
        userRepository.save(user);

        logger.info("Rol '{}' removido del usuario '{}'", roleName, user.getUsername());
        return toDTO(user);
    }

    //
    @Override
    @Transactional
    public UserDTO createUserManual(UserCreateDTO request) {
        // 1. Validaciones de existencia
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new DuplicateUserException(request.getUsername());
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateUserException(request.getEmail());
        }
        validatePassword(request.getPassword());

        // 2. Usamos el mapper para crear la entidad base
        UserEntity user = userMapper.toEntity(request);
        
        // 3. Configuraciones de seguridad y estado
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActivo(request.getActivo() != null ? request.getActivo() : true);
        user.setEmailVerified(true); 

        // 4. Asignar roles dinámicamente
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<RoleEntity> roles = request.getRoles().stream()
                    .map(roleEnum -> roleRepository.findByName(roleEnum.name())
                            .orElseThrow(() -> new RoleNotFoundException(roleEnum.name())))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        } else {
            RoleEntity userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RoleNotFoundException("USER"));
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        }

        // 5. Vincular el perfil (Bidireccionalidad)
        UserProfile profile = UserProfile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();
        
        user.setProfile(profile);

        // 6. Guardar en BD (Persiste User y Profile por el Cascade)
        UserEntity savedUser = userRepository.save(user);

        logger.info("Usuario creado manualmente con perfil: id={}, username={}", 
                    savedUser.getId(), savedUser.getUsername());

        // 7. 🔹 LA SOLUCIÓN DEFINITIVA: Mapear a DTO dentro de la transacción
        return userMapper.toDTO(savedUser);
    }


    // ====================== Búsqueda genérica ======================
    private String normalizeSearch(String input) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .orElse("");
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<UserDTO> findByUsernameContaining(String search, Pageable pageable) {
        String query = normalizeSearch(search);
        return userRepository.findByUsernameContainingIgnoreCase(query, pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<UserDTO> findByUsernameOrEmail(String search, Pageable pageable) {
        String query = normalizeSearch(search);
        return userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query, pageable)
                .map(this::toDTO);
    }

    // ====================== DTO Mapper ======================
    @Override
    public UserDTO toDTO(UserEntity user) {
        // 1. Mapeamos los roles a String como ya hacías
        Set<String> roles = Optional.ofNullable(user.getRoles())
                .orElse(Collections.emptySet())
                .stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());

        // 2. Extraemos el perfil para acceder a los campos de nombre, apellido y teléfono
        var profile = user.getProfile();

        // 3. Construimos el DTO incluyendo los campos del perfil
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .activo(user.isActivo())
                .emailVerified(user.isEmailVerified())
                .roles(roles)
                // ✅ Añadimos los campos del perfil (con protección contra nulos)
                .firstName(profile != null ? profile.getFirstName() : null)
                .lastName(profile != null ? profile.getLastName() : null)
                .phone(profile != null ? profile.getPhone() : null)
                .build();
    }
}
