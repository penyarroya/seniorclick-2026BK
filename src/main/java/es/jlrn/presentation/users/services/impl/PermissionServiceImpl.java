package es.jlrn.presentation.users.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.persistence.models.users.models.PermissionEntity;
import es.jlrn.persistence.models.users.repositories.PermissionRepository;
import es.jlrn.presentation.users.services.interfaces.IpermissionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IpermissionService {
//
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionEntity> findAll() { // Si prefieres usar DTOs, mapea aquí
        return permissionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionEntity findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));
    }

    @Override
    @Transactional
    public PermissionEntity save(PermissionEntity permission) {
        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }
}