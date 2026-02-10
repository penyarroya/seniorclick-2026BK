package es.jlrn.persistence.models.users.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.jlrn.persistence.models.users.models.RoleEntity;


@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
//
    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);
}