package es.jlrn.persistence.models.universilabs.repositories;

import es.jlrn.persistence.models.universilabs.models.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long>,
                                               JpaSpecificationExecutor<Institution> {
//    
    Optional<Institution> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByName(String name);
    //
    Optional<Institution> findFirstByOrderByIdAsc();
}