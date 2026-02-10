package es.jlrn.presentation.universilabs.mappers;

import org.springframework.stereotype.Component;
import es.jlrn.persistence.models.universilabs.models.Contribution;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IContributionMapper;

@Component
public class ContributionMapperImpl implements IContributionMapper {

    @Override
    public ContributionResponseDTO toResponseDTO(Contribution entity) {
        if (entity == null) return null;

        return new ContributionResponseDTO(
            entity.getId(),
            entity.getContent(),
            entity.getCreatedAt(),
            entity.getPage().getId(),
            entity.getPage().getTitle(), // <--- 1. IMPORTANTE: Extraemos el título de la página
            entity.getUser().getId(),
            entity.getUser().getUsername() // 2. IMPORTANTE: Esto llegará como 'username' a Angular
        );
    }

    @Override
    public Contribution toEntity(ContributionRequestDTO dto) {
        if (dto == null) return null;

        return Contribution.builder()
            .content(dto.content()) // En records de Java se accede como metodo(), no getMetodo()
            // Nota: pageId y userId se gestionan en el Service mediante JPA
            .build();
    }
}