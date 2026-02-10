package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;

import es.jlrn.presentation.universilabs.dtos.collections.CollectionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionResponseDTO;

public interface ICollectionService {
//    
    List<CollectionResponseDTO> findByProjectId(Long projectId);
    CollectionResponseDTO findById(Long id);
    CollectionResponseDTO save(CollectionRequestDTO dto);
    CollectionResponseDTO update(Long id, CollectionRequestDTO dto);
    void delete(Long id);
    public Page<CollectionResponseDTO> findAllPaginated(int page, int size, String search);
}