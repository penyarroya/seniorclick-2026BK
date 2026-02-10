package es.jlrn.persistence.models.universilabs.repositories;

import es.jlrn.persistence.models.universilabs.models.Contribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {
//    
    List<Contribution> findByPageIdOrderByCreatedAtDesc(Long pageId);

    //Collection<ContributionResponseDTO> findAllByOrderByCreatedAtDesc();

    List<Contribution> findAllByOrderByCreatedAtDesc();
}