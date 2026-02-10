package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;

import es.jlrn.presentation.universilabs.dtos.resources.ResourceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IResourceService {
//    
    ResourceDTO create(ResourceDTO dto);
    ResourceDTO findById(Long id);
    List<ResourceDTO> findByPageId(Long pageId);
    ResourceDTO update(Long id, ResourceDTO dto);
    void delete(Long id);
    Page<ResourceDTO> findAll(Pageable pageable);
}