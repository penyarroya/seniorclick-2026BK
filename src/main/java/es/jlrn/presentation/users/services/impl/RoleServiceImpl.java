package es.jlrn.presentation.users.services.impl;

import es.jlrn.persistence.models.users.models.PermissionEntity;
import es.jlrn.persistence.models.users.models.RoleEntity;
import es.jlrn.persistence.models.users.repositories.RoleRepository;
import es.jlrn.presentation.users.dtos.Roles.RoleDTO;
import es.jlrn.presentation.users.services.interfaces.IRoleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {
//
    private final RoleRepository roleRepository;

    // Conversión Entity -> DTO
    private RoleDTO mapToDTO(RoleEntity role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .permissions(role.getPermissions()
                        .stream()
                        .map(PermissionEntity::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    // Conversión DTO -> Entity (solo campos básicos, permisos se pueden mapear aparte)
    private RoleEntity mapToEntity(RoleDTO dto) {
        RoleEntity entity = new RoleEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        // NOTA: aquí deberías mapear los permisos a PermissionEntity si es necesario
        return entity;
    }

    // Obtener todos los roles
    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Obtener rol por ID
    @Override
    public Optional<RoleDTO> getRoleById(Long id) {
        return roleRepository.findById(id)
                .map(this::mapToDTO);
    }

    // Crear rol
    @Override
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        RoleEntity entity = mapToEntity(roleDTO);
        RoleEntity saved = roleRepository.save(entity);
        return mapToDTO(saved);
    }

    // Actualizar rol
    @Override
    @Transactional
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        RoleEntity existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
        existingRole.setName(roleDTO.getName());
        // NOTA: actualizar permisos si es necesario
        RoleEntity updated = roleRepository.save(existingRole);
        return mapToDTO(updated);
    }

    // Eliminar rol
    @Override
    @Transactional
    public void deleteRole(Long id) {
        RoleEntity existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
        roleRepository.delete(existingRole);
    }
}
