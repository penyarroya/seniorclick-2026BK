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
        
        return new UserProfileDTO(
            entity.getUserId(),    // Mapeamos el ID de vinculación
            entity.getFirstName(),
            entity.getLastName(),
            entity.getPhone(),
            entity.getAvatarUrl()
        );
    }

    @Override
    public UserProfile toEntity(UserProfileDTO dto) {
        if (dto == null) return null;
        
        // Usando el constructor o builder de tu entidad UserProfile
        UserProfile entity = new UserProfile();
        entity.setFirstName(dto.firstName());
        entity.setLastName(dto.lastName());
        entity.setPhone(dto.phone());
        entity.setAvatarUrl(dto.avatarUrl());
        
        // El userId se suele asignar en el Service antes del save() 
        // para garantizar la vinculación con el usuario correcto.
        return entity;
    }
}