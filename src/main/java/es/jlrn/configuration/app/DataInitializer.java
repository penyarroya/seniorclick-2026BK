// package es.jlrn.configuration.app;

// import jakarta.annotation.PostConstruct;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import es.jlrn.persistence.enums.Permission;
// import es.jlrn.persistence.enums.Role;
// import es.jlrn.persistence.models.PermissionEntity;
// import es.jlrn.persistence.models.RoleEntity;
// import es.jlrn.persistence.models.UserEntity;
// import es.jlrn.persistence.repositories.PermissionRepository;
// import es.jlrn.persistence.repositories.RoleRepository;
// import es.jlrn.persistence.repositories.UserRepository;

// import java.util.Set;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// @ConditionalOnProperty(name = "app.init-data", havingValue = "true")
// public class DataInitializer {
// //
//     private final RoleRepository roleRepository;
//     private final PermissionRepository permissionRepository;
//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;

//     @PostConstruct
//     @Transactional
//     public void init() {
//         log.info("🚀 Iniciando DataInitializer dinámico...");

//         crearPermisos();
//         crearRoles();
//         crearUsuarioPorDefecto();
//     }

//     // ====================
//     // Crear permisos dinámicamente
//     // ====================
//     private void crearPermisos() {
//         Stream.of(Permission.values()).forEach(permEnum -> 
//             permissionRepository.findByName(permEnum.name())
//                 .orElseGet(() -> {
//                     PermissionEntity p = PermissionEntity.builder()
//                             .name(permEnum.name())
//                             .build();
//                     permissionRepository.save(p);
//                     log.info("✅ Permiso creado: {}", permEnum.name());
//                     return p;
//                 })
//         );
//     }

//     // ====================
//     // Crear roles dinámicamente según permisos por defecto
//     // ====================
//     private void crearRoles() {
//         Stream.of(Role.values()).forEach(roleEnum -> 
//             roleRepository.findByName(roleEnum.name())
//                 .orElseGet(() -> {
//                     RoleEntity role = RoleEntity.builder()
//                             .name(roleEnum.name())
//                             .permissions(convertirPermisos(roleEnum.getDefaultPermissions()))
//                             .build();
//                     roleRepository.save(role);
//                     log.info("✅ Rol creado: {}", roleEnum.name());
//                     return role;
//                 })
//         );
//     }

//     // ====================
//     // Convertir Set<Permission> a Set<PermissionEntity>
//     // ====================
//     private Set<PermissionEntity> convertirPermisos(Set<Permission> perms) {
//         return perms.stream()
//                 .map(perm -> permissionRepository.findByName(perm.name())
//                         .orElseThrow(() -> new RuntimeException("❌ Permiso no encontrado: " + perm.name())))
//                 .collect(Collectors.toSet());
//     }

//     // ====================
//     // Crear usuario SUPER_ADMIN por defecto
//     // ====================
//     private void crearUsuarioPorDefecto() {
//         String username = "pnry";
//         if (userRepository.existsByUsername(username)) {
//             log.info("ℹ️ Usuario por defecto ya existe: {}", username);
//             return;
//         }

//         RoleEntity superRole = roleRepository.findByName(Role.SUPER_ADMIN.name())
//                 .orElseThrow(() -> new RuntimeException("❌ Rol no encontrado: SUPER_ADMIN"));

//         UserEntity user = UserEntity.builder()
//                 .username(username)
//                 .email("nn@gmail.com")
//                 .password(passwordEncoder.encode("12345678"))
//                 .activo(true)
//                 .roles(Set.of(superRole))
//                 .build();

//         userRepository.save(user);
//         log.info("✅ Usuario SUPER_ADMIN creado: {}", username);
//     }
// }


package es.jlrn.configuration.app;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.persistence.enums.Permission;
import es.jlrn.persistence.enums.Role;
import es.jlrn.persistence.models.users.models.PermissionEntity;
import es.jlrn.persistence.models.users.models.RoleEntity;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.repositories.PermissionRepository;
import es.jlrn.persistence.models.users.repositories.RoleRepository;
import es.jlrn.persistence.models.users.repositories.UserRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.init-data", havingValue = "true")
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("🚀 Iniciando DataInitializer dinámico...");

        Map<String, PermissionEntity> permisosMap = crearPermisos();
        Map<String, RoleEntity> rolesMap = crearRoles(permisosMap);
        crearUsuarioPorDefecto(rolesMap);
    }

    // ====================
    // Crear permisos dinámicamente y devolver un Map para optimizar consultas
    // ====================
    private Map<String, PermissionEntity> crearPermisos() {
        return Stream.of(Permission.values())
                .map(permEnum -> permissionRepository.findByName(permEnum.name())
                        .orElseGet(() -> {
                            PermissionEntity p = PermissionEntity.builder()
                                    .name(permEnum.name())
                                    .build();
                            permissionRepository.save(p);
                            log.info("✅ Permiso creado: {}", permEnum.name());
                            return p;
                        })
                )
                .collect(Collectors.toMap(PermissionEntity::getName, p -> p));
    }

    // ====================
    // Crear roles dinámicamente según permisos por defecto
    // ====================
    private Map<String, RoleEntity> crearRoles(Map<String, PermissionEntity> permisosMap) {
        return Stream.of(Role.values())
                .map(roleEnum -> roleRepository.findByName(roleEnum.name())
                        .orElseGet(() -> {
                            RoleEntity role = RoleEntity.builder()
                                    .name(roleEnum.name())
                                    .permissions(convertirPermisos(roleEnum.getDefaultPermissions(), permisosMap))
                                    .build();
                            roleRepository.save(role);
                            log.info("✅ Rol creado: {}", roleEnum.name());
                            return role;
                        })
                )
                .collect(Collectors.toMap(RoleEntity::getName, r -> r));
    }

    // ====================
    // Convertir Set<Permission> a Set<PermissionEntity> usando Map para evitar consultas repetidas
    // ====================
    private Set<PermissionEntity> convertirPermisos(Set<Permission> perms, Map<String, PermissionEntity> permisosMap) {
        return perms.stream()
                .map(perm -> {
                    PermissionEntity p = permisosMap.get(perm.name());
                    if (p == null) throw new RuntimeException("❌ Permiso no encontrado: " + perm.name());
                    return p;
                })
                .collect(Collectors.toSet());
    }

    // ====================
    // Crear usuario SUPER_ADMIN por defecto
    // ====================
    private void crearUsuarioPorDefecto(Map<String, RoleEntity> rolesMap) {
        String username = "pnry";
        if (userRepository.existsByUsername(username)) {
            log.info("ℹ️ Usuario por defecto ya existe: {}", username);
            return;
        }

        RoleEntity superRole = rolesMap.get(Role.SUPER_ADMIN.name());
        if (superRole == null) throw new RuntimeException("❌ Rol no encontrado: SUPER_ADMIN");

        UserEntity user = UserEntity.builder()
                .username(username)
                .email("nn@gmail.com")
                .password(passwordEncoder.encode("12345678"))
                .activo(true)
                .roles(Set.of(superRole))
                .build();

        userRepository.save(user);
        log.info("✅ Usuario SUPER_ADMIN creado: {}", username);
    }
}
