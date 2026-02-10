package es.jlrn.presentation.universilabs.mappers.interfaces;

import es.jlrn.persistence.models.universilabs.models.UserProfile;
import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;

public interface IUserProfileMapper {
    UserProfileDTO toDTO(UserProfile entity);
    UserProfile toEntity(UserProfileDTO dto);
}