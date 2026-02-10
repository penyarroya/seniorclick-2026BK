package es.jlrn.presentation.universilabs.mappers;

import es.jlrn.persistence.models.universilabs.models.Institution;
import es.jlrn.presentation.universilabs.dtos.institutions.InstitutionDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.InstitutionMapper;

import org.springframework.stereotype.Component;

@Component
public class InstitutionMapperImpl implements InstitutionMapper {

    @Override
    public InstitutionDTO toDTO(Institution entity) {
        if (entity == null) return null;
        
        return InstitutionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .website(entity.getWebsite())
                .description(entity.getDescription())
                .email(entity.getEmail())
                .build();
    }

    @Override
    public Institution toEntity(InstitutionDTO dto) {
        if (dto == null) return null;

        return Institution.builder()
                .id(dto.getId()) // Importante incluirlo para actualizaciones
                .name(dto.getName())
                .address(dto.getAddress())
                .website(dto.getWebsite())
                .description(dto.getDescription())
                .email(dto.getEmail())
                .build();
    }
}