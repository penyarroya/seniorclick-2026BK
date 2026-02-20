// package es.jlrn.presentation.universilabs.services.impl;

// import es.jlrn.exceptions.exception.ResourceNotFoundException;
// import es.jlrn.persistence.models.universilabs.models.UserProfile;
// import es.jlrn.persistence.models.universilabs.repositories.UserProfileRepository;
// import es.jlrn.persistence.models.users.models.UserEntity;
// import es.jlrn.persistence.models.users.repositories.UserRepository;
// import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;
// import es.jlrn.presentation.universilabs.services.interfaces.IUserProfileService;
// import es.jlrn.presentation.universilabs.mappers.interfaces.IUserProfileMapper; // Importar interfaz mapper
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;

// @Service
// @RequiredArgsConstructor
// public class UserProfileServiceImpl implements IUserProfileService {
// //
//     private final UserProfileRepository profileRepository;
//     private final UserRepository userRepository;
//     private final IUserProfileMapper profileMapper; // Inyectar el mapper

//     // @Override
//     // @Transactional(readOnly = true)
//     // public UserProfileDTO getProfile(Long userId) {
//     //     UserProfile profile = profileRepository.findById(userId)
//     //             .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para el usuario: " + userId));
        
//     //     return profileMapper.toDTO(profile);
//     // }

//     // @Override
//     // @Transactional(readOnly = true)
//     // public UserProfileDTO getProfile(Long userId) {
//     //     return profileRepository.findById(userId)
//     //             .map(profileMapper::toDTO)
//     //             .orElseGet(() -> new UserProfileDTO(userId, "", "", "", null));
//     // }

//     @Override
//     @Transactional(readOnly = true)
//     public UserProfileDTO getProfile(Long userId) {
//         return profileRepository.findById(userId)
//                 .map(profileMapper::toDTO)
//                 .orElseGet(() -> UserProfileDTO.builder()
//                     .id(userId)
//                     .userId(userId)
//                     .firstName("")
//                     .lastName("")
//                     .phone("")
//                     .avatarUrl(null)
//                     .nombre("Usuario Nuevo") // Opcional: para que no salga vacío en Angular
//                     .activo(true)
//                     .build());
//     }
   
//     @Override
//     public Page<UserProfileDTO> findAll(Pageable pageable) {
//         // 1. Buscamos en la base de datos de forma paginada
//         // 2. Usamos .map() de la clase Page para transformar cada Entidad a DTO
//         return profileRepository.findAll(pageable)
//                 .map(entity -> profileMapper.toDTO(entity));
//     }

//     // @Override
//     // @Transactional
//     // public UserProfileDTO createProfile(Long userId, UserProfileDTO dto) {
//     //     // 1. Verificamos si ya existe para asegurar idempotencia
//     //     if (profileRepository.existsById(userId)) {
//     //         return updateProfile(userId, dto);
//     //     }

//     //     // 2. Usamos el mapper para convertir DTO a Entidad
//     //     UserProfile profile = profileMapper.toEntity(dto);
        
//     //     // 3. Vinculamos manualmente el ID del usuario (esto es clave)
//     //     profile.setUserId(userId); 

//     //     // 4. Guardamos y retornamos el resultado mapeado a DTO
//     //     UserProfile savedProfile = profileRepository.save(profile);
//     //     return profileMapper.toDTO(savedProfile);
//     // }

//     // 1. Añade el repositorio de usuarios al inicio de la clase
   

//     @Override
//     @Transactional
//     public UserProfileDTO createProfile(Long userId, UserProfileDTO dto) {
//         // 1. Verificar si ya existe (idempotencia)
//         if (profileRepository.existsById(userId)) {
//             return updateProfile(userId, dto);
//         }

//         // 2. Buscar al usuario real (OBLIGATORIO para @MapsId)
//         UserEntity user = userRepository.findById(userId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Usuario no existe con ID: " + userId));

//         // 3. Convertir DTO a Entidad
//         UserProfile profile = profileMapper.toEntity(dto);
        
//         // 4. VINCULACIÓN MANUAL (Aquí es donde se arregla el 500)
//         profile.setUser(user);      // Hibernate sacará el ID de aquí
//         profile.setUserId(userId);  // Sincronizamos el ID primario

//         // 5. Guardar y devolver
//         UserProfile savedProfile = profileRepository.save(profile);
//         return profileMapper.toDTO(savedProfile);
//     }

//     @Override
//     @Transactional
//     public UserProfileDTO updateProfile(Long userId, UserProfileDTO dto) {
//         UserProfile profile = profileRepository.findById(userId)
//                 .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para el usuario: " + userId));

//         // Actualizamos los campos de la entidad gestionada por Hibernate
//         profile.setFirstName(dto.firstName());
//         profile.setLastName(dto.lastName());
//         profile.setPhone(dto.phone());
//         profile.setAvatarUrl(dto.avatarUrl());

//         // Al retornar usamos el mapper para asegurar que el userId vaya en el DTO
//         return profileMapper.toDTO(profileRepository.save(profile));
//     }

//     @Override
//     @Transactional
//     public void deleteProfile(Long userId) {
//         if (!profileRepository.existsById(userId)) {
//             throw new ResourceNotFoundException("No se puede eliminar: Perfil no encontrado para el usuario: " + userId);
//         }
//         profileRepository.deleteById(userId);
//     }

// }

package es.jlrn.presentation.universilabs.services.impl;

import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.UserProfile;
import es.jlrn.persistence.models.universilabs.repositories.UserProfileRepository;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IUserProfileService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import es.jlrn.presentation.universilabs.mappers.interfaces.IUserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements IUserProfileService {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final IUserProfileMapper profileMapper;

    // @Override
    // @Transactional(readOnly = true)
    // public UserProfileDTO getProfile(Long userId) {
    //     return profileRepository.findById(userId)
    //             .map(profileMapper::toDTO)
    //             .orElseGet(() -> UserProfileDTO.builder()
    //                 .id(userId)
    //                 .userId(userId)
    //                 .firstName("")
    //                 .lastName("")
    //                 .phone("")
    //                 .avatarUrl(null)
    //                 .nombre("Usuario Nuevo")
    //                 .activo(true)
    //                 .build());
    // }


    @Transactional(readOnly = true)
    public UserProfileDTO getProfileByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> getProfile(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getProfile(Long userId) {
        // CAMBIO: Buscar el usuario primero para acceder a su perfil vinculado
        return userRepository.findById(userId)
                .map(UserEntity::getProfile) // Accedemos al perfil desde el usuario
                .map(profileMapper::toDTO)
                .orElseGet(() -> {
                    // Si el usuario no tiene perfil, devolvemos el objeto por defecto
                    return UserProfileDTO.builder()
                        .id(userId)
                        .userId(userId)
                        .firstName("")
                        .lastName("")
                        .nombre("Usuario Nuevo")
                        .activo(true)
                        .build();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserProfileDTO> findAll(Pageable pageable) {
        return profileRepository.findAll(pageable)
                .map(profileMapper::toDTO);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public UserProfileDTO createProfile(Long userId, UserProfileDTO dto) {
        // 1. Buscamos el usuario
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userId));

        // 2. Buscamos si ya tiene perfil. Si lo tiene, lo usamos; si no, creamos uno.
        UserProfile profile = profileRepository.findById(userId).orElse(new UserProfile());
        
        // 3. Sincronizamos los datos
        profile.setUser(user);
        profile.setUserId(userId);
        profile.setFirstName(dto.firstName());
        profile.setLastName(dto.lastName());
        profile.setPhone(dto.phone());
        profile.setAvatarUrl(dto.avatarUrl());

        // 4. LIMPIEZA DE CACHÉ (Esto evita el OptimisticLocking de registros fantasma)
        profileRepository.saveAndFlush(profile); // Forzamos escritura
        entityManager.clear(); // Limpiamos el contexto para que no haya conflictos de @Version

        // 5. Recuperamos el resultado limpio para el DTO
        UserProfile savedProfile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error al recuperar el perfil guardado"));

        return profileMapper.toDTO(savedProfile);
    }

    @Transactional
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO dto) {
        // 1. Buscamos al usuario. Al traer al usuario, JPA ya conoce su perfil (si existe)
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // 2. Obtenemos el perfil que ya tiene el usuario
        UserProfile profile = user.getProfile();
        
        if (profile == null) {
            // Por seguridad, si por algún error el registro no creó el perfil, lo creamos aquí
            profile = new UserProfile();
            profile.setUser(user);
            profile.setUserId(userId);
            user.setProfile(profile);
        }

        // 3. Actualizamos los campos con los datos del DTO
        profile.setFirstName(dto.firstName());
        profile.setLastName(dto.lastName());
        profile.setPhone(dto.phone());
        profile.setAvatarUrl(dto.avatarUrl());

        // 4. Guardamos al USUARIO (Cascada). 
        // Esto actualiza el perfil y sube la versión del usuario para evitar el OptimisticLockException
        userRepository.save(user);

        return profileMapper.toDTO(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long userId) {
        if (!profileRepository.existsById(userId)) {
            throw new ResourceNotFoundException("No se puede eliminar: Perfil no encontrado");
        }
        profileRepository.deleteById(userId);
    }
}