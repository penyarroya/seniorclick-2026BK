package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;

import es.jlrn.presentation.universilabs.dtos.pages.PageDetailDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageRequestDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;

public interface IPageService {
//    
    List<PageResponseDTO> findBySubtopicId(Long subtopicId);
    PageResponseDTO findById(Long id);
    PageResponseDTO save(PageRequestDTO dto);
    PageResponseDTO update(Long id, PageRequestDTO dto);
    void delete(Long id);
    // Añade este método a la interfaz
    PageDetailDTO getPageDetailForUser(Long pageId, Long userId);
    public org.springframework.data.domain.Page<PageResponseDTO> findAll(org.springframework.data.domain.Pageable pageable);
}