package es.jlrn.presentation.universilabs.services.impl;

import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.UserProfile;
import es.jlrn.persistence.models.universilabs.repositories.UserProfileRepository;
import es.jlrn.presentation.universilabs.dtos.userprofile.UserProfileDTO;
import es.jlrn.presentation.universilabs.services.interfaces.IUserProfileService;
import es.jlrn.presentation.universilabs.mappers.interfaces.IUserProfileMapper; // Importar interfaz mapper
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements IUserProfileService {
//
    private final UserProfileRepository profileRepository;
    private final IUserProfileMapper profileMapper; // Inyectar el mapper

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getProfile(Long userId) {
        UserProfile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para el usuario: " + userId));
        
        return profileMapper.toDTO(profile);
    }

    @Override
    public Page<UserProfileDTO> findAll(Pageable pageable) {
        // 1. Buscamos en la base de datos de forma paginada
        // 2. Usamos .map() de la clase Page para transformar cada Entidad a DTO
        return profileRepository.findAll(pageable)
                .map(entity -> profileMapper.toDTO(entity));
    }

    @Override
    @Transactional
    public UserProfileDTO createProfile(Long userId, UserProfileDTO dto) {
        // 1. Verificamos si ya existe para asegurar idempotencia
        if (profileRepository.existsById(userId)) {
            return updateProfile(userId, dto);
        }

        // 2. Usamos el mapper para convertir DTO a Entidad
        UserProfile profile = profileMapper.toEntity(dto);
        
        // 3. Vinculamos manualmente el ID del usuario (esto es clave)
        profile.setUserId(userId); 

        // 4. Guardamos y retornamos el resultado mapeado a DTO
        UserProfile savedProfile = profileRepository.save(profile);
        return profileMapper.toDTO(savedProfile);
    }

    @Override
    @Transactional
    public UserProfileDTO updateProfile(Long userId, UserProfileDTO dto) {
        UserProfile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para el usuario: " + userId));

        // Actualizamos los campos de la entidad gestionada por Hibernate
        profile.setFirstName(dto.firstName());
        profile.setLastName(dto.lastName());
        profile.setPhone(dto.phone());
        profile.setAvatarUrl(dto.avatarUrl());

        // Al retornar usamos el mapper para asegurar que el userId vaya en el DTO
        return profileMapper.toDTO(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public void deleteProfile(Long userId) {
        if (!profileRepository.existsById(userId)) {
            throw new ResourceNotFoundException("No se puede eliminar: Perfil no encontrado para el usuario: " + userId);
        }
        profileRepository.deleteById(userId);
    }

}