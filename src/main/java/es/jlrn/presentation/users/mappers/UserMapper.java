// package es.jlrn.presentation.users.mappers;

// import org.springframework.stereotype.Component;

// import es.jlrn.persistence.models.users.models.RoleEntity;
// import es.jlrn.persistence.models.users.models.UserEntity;
// import es.jlrn.presentation.users.dtos.Usuarios.UserCreateDTO;
// import es.jlrn.presentation.users.dtos.Usuarios.UserDTO;
// import es.jlrn.presentation.users.dtos.Usuarios.UserUpdateDTO;

// import java.util.stream.Collectors;

// @Component
// public class UserMapper {

//     public UserDTO toDTO(UserEntity user) {
//         if (user == null) return null;

//         return UserDTO.builder()
//                 .id(user.getId())
//                 .username(user.getUsername())
//                 .email(user.getEmail())
//                 .activo(user.isActivo())
//                 .emailVerified(user.isEmailVerified()) // ✅ nuevo
//                 .verificationCode(user.getVerificationCode()) // ✅ nuevo
//                 .verificationCodeExpiry(user.getVerificationCodeExpiry()) // ✅ nuevo
//                 .roles(user.getRoles()
//                         .stream()
//                         .map(RoleEntity::getName)
//                         .collect(Collectors.toSet()))
//                 .fechaAlta(user.getFechaAlta())
//                 .fechaActualizacion(user.getFechaActualizacion())
//                 .build();
//     }

//     public UserEntity toEntity(UserCreateDTO dto) {
//         if (dto == null) return null;

//         Boolean activo = dto.getActivo() != null ? dto.getActivo() : true;

//         return UserEntity.builder()
//                 .username(dto.getUsername())
//                 .password(dto.getPassword())
//                 .email(dto.getEmail())
//                 .activo(activo)
//                 .build();
//     }

//     public void updateEntityFromDTO(UserUpdateDTO dto, UserEntity entity) {
//         if (dto == null || entity == null) return;

//         entity.setUsername(dto.getUsername());
//         entity.setEmail(dto.getEmail());

//         if (dto.getActivo() != null) {
//             entity.setActivo(dto.getActivo());
//         }
//     }
// }

package es.jlrn.presentation.users.mappers;

import org.springframework.stereotype.Component;

import es.jlrn.persistence.models.users.models.RoleEntity;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.presentation.users.dtos.Usuarios.UserCreateDTO;
import es.jlrn.presentation.users.dtos.Usuarios.UserDTO;
import es.jlrn.presentation.users.dtos.Usuarios.UserUpdateDTO;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDTO toDTO(UserEntity user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .activo(user.isActivo())
                .emailVerified(user.isEmailVerified())
                .verificationCode(user.getVerificationCode())
                .verificationCodeExpiry(user.getVerificationCodeExpiry())
                .roles(user.getRoles()
                        .stream()
                        .map(RoleEntity::getName)
                        .collect(Collectors.toSet()))
                .fechaAlta(user.getFechaAlta())
                .fechaActualizacion(user.getFechaActualizacion())
                // 🔹 CAMBIO: Mapeo de datos personales desde el perfil anidado
                .firstName(user.getProfile() != null ? user.getProfile().getFirstName() : null)
                .lastName(user.getProfile() != null ? user.getProfile().getLastName() : null)
                .phone(user.getProfile() != null ? user.getProfile().getPhone() : null)
                .build();
    }

    public UserEntity toEntity(UserCreateDTO dto) {
        if (dto == null) return null;

        Boolean activo = dto.getActivo() != null ? dto.getActivo() : true;

        return UserEntity.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .activo(activo)
                // Nota: El perfil se vincula en el Service para asegurar la bidireccionalidad
                .build();
    }

    public void updateEntityFromDTO(UserUpdateDTO dto, UserEntity entity) {
        if (dto == null || entity == null) return;

        // Actualización de campos de UserEntity
        if (dto.getUsername() != null) entity.setUsername(dto.getUsername());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getActivo() != null) entity.setActivo(dto.getActivo());

        // 🔹 CAMBIO: Actualización de campos de UserProfile si existe
        if (entity.getProfile() != null) {
            if (dto.getFirstName() != null) entity.getProfile().setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) entity.getProfile().setLastName(dto.getLastName());
            if (dto.getPhone() != null) entity.getProfile().setPhone(dto.getPhone());
        }
    }
}