package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;

import es.jlrn.presentation.universilabs.dtos.institutions.InstitutionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InstitutionService {
//
    List<InstitutionDTO> getAll();
    InstitutionDTO getById(Long id);
    InstitutionDTO create(InstitutionDTO dto);
    InstitutionDTO update(Long id, InstitutionDTO dto);
    void delete(Long id);
    InstitutionDTO getCurrent();
    // Firma actualizada para soportar filtros
    public Page<InstitutionDTO> getAllPaginated(Pageable pageable, String global, String name, String email);
    //
    public boolean existsByEmail(String email);
    public boolean existsByName(String name);
}