package es.jlrn.presentation.universilabs.services.interfaces;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.jlrn.presentation.universilabs.dtos.projects.ProjectRequestDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectResponseDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectStructureDTO;


public interface IProjectService {

    // Devuelve una lista de DTOs con nombres de institución y creador
    public Page<ProjectResponseDTO> findAll(Pageable pageable);
     public Page<ProjectResponseDTO> findAll(String searchTerm, Pageable pageable);

    // Devuelve un solo DTO por ID
    ProjectResponseDTO findById(Long id);

    // Recibe los datos de creación (IDs) y devuelve el objeto creado
    ProjectResponseDTO save(ProjectRequestDTO projectRequestDTO);

    // Recibe los datos de actualización y el ID
    ProjectResponseDTO update(Long id, ProjectRequestDTO projectRequestDTO);

    // El delete permanece igual ya que solo necesita el ID
    void delete(Long id);

    List<ProjectResponseDTO> findAllList();
    // Puedes renombrarlo para que sea más claro
    List<ProjectResponseDTO> findAllActiveList();
    ProjectStructureDTO findStructureById(Long id);
}