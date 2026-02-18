// package es.jlrn.presentation.universilabs.mappers.interfaces;

// import org.springframework.stereotype.Component;
// import es.jlrn.persistence.models.universilabs.models.UserProfile;
// import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;

// @Component
// public class UserProfileMapperImpl implements IUserProfileMapper {
// //
//     @Override
//     public UserProfileDTO toDTO(UserProfile entity) {
//         if (entity == null) return null;
        
//         return new UserProfileDTO(
//             entity.getUserId(),    // Mapeamos el ID de vinculación
//             entity.getFirstName(),
//             entity.getLastName(),
//             entity.getPhone(),
//             entity.getAvatarUrl()
//         );
//     }

//     @Override
//     public UserProfile toEntity(UserProfileDTO dto) {
//         if (dto == null) return null;
        
//         // Usando el constructor o builder de tu entidad UserProfile
//         UserProfile entity = new UserProfile();
//         entity.setFirstName(dto.firstName());
//         entity.setLastName(dto.lastName());
//         entity.setPhone(dto.phone());
//         entity.setAvatarUrl(dto.avatarUrl());
        
//         // El userId se suele asignar en el Service antes del save() 
//         // para garantizar la vinculación con el usuario correcto.
//         return entity;
//     }
// }

package es.jlrn.presentation.universilabs.mappers.interfaces;

import org.springframework.stereotype.Component;
import es.jlrn.persistence.models.universilabs.models.UserProfile;
import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;

@Component
public class UserProfileMapperImpl implements IUserProfileMapper {
//
    @Override
    public UserProfileDTO toDTO(UserProfile entity) {
        if (entity == null) return null;
        
        // Construimos el DTO con todos los campos que el Record ahora posee
        return UserProfileDTO.builder()
            .id(entity.getUserId()) // Crucial para que Angular no genere el "1:1"
            .userId(entity.getUserId())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .phone(entity.getPhone())
            .avatarUrl(entity.getAvatarUrl())
            // Evitamos valores nulos en el string concatenado
            .nombre((entity.getFirstName() != null ? entity.getFirstName() : "") + " " + 
                    (entity.getLastName() != null ? entity.getLastName() : "").trim())
            .activo(true)
            .build();
    }

    @Override
    public UserProfile toEntity(UserProfileDTO dto) {
        if (dto == null) return null;
        
        UserProfile entity = new UserProfile();
        // Cambia dto.firstName() por dto.getFirstName() si es una Clase normal
        // o déjalo así si confirmas que es un Record.
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setPhone(dto.phone());
        entity.setAvatarUrl(dto.avatarUrl());
        
        // MUY IMPORTANTE: Seteamos el ID si viene en el DTO
        if (dto.userId() != null) {
            entity.setUserId(dto.userId());
        } else if (dto.id() != null) {
            entity.setUserId(dto.id());
        }
        
        return entity;
    }
}