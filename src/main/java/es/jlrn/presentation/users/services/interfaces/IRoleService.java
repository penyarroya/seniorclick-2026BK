package es.jlrn.presentation.users.services.interfaces;

import java.util.List;
import java.util.Optional;

import es.jlrn.presentation.users.dtos.Roles.RoleDTO;

public interface IRoleService {
//
    // Obtener todos los roles
    List<RoleDTO> getAllRoles();

    // Obtener rol por ID
    Optional<RoleDTO> getRoleById(Long id);

    // Crear rol
    RoleDTO createRole(RoleDTO role);

    // Actualizar rol
    RoleDTO updateRole(Long id, RoleDTO roleDetails);

    // Eliminar rol
    void deleteRole(Long id);
    
}
