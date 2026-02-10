package es.jlrn.persistence.models.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.jlrn.persistence.models.users.models.PermissionEntity;


@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
//
    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);
}