package es.jlrn.presentation.users.services.interfaces;

import java.util.List;

import es.jlrn.persistence.models.users.models.PermissionEntity;

public interface IpermissionService {
//
    public List<PermissionEntity> findAll();
    public PermissionEntity save(PermissionEntity permission);
     public void deleteById(Long id) ;
     PermissionEntity findById(Long id);
}
