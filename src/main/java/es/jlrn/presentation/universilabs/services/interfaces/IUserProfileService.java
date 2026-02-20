package es.jlrn.presentation.universilabs.services.interfaces;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;

public interface IUserProfileService {
//    
    UserProfileDTO getProfile(Long userId);
    UserProfileDTO updateProfile(Long userId, UserProfileDTO dto);
    UserProfileDTO createProfile(Long userId, UserProfileDTO dto);
    void deleteProfile(Long userId);
    Page<UserProfileDTO> findAll(Pageable pageable);
    UserProfileDTO getProfileByUsername(String username);
}