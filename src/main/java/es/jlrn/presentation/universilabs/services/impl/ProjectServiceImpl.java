package es.jlrn.presentation.universilabs.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.jlrn.exceptions.exception.InstitutionNotFoundException;
import es.jlrn.exceptions.exception.ResourceConflictException;
import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.exceptions.exception.UserNotFoundException;
import es.jlrn.persistence.models.universilabs.models.Project;
import es.jlrn.persistence.models.universilabs.repositories.ProjectRepository;
import es.jlrn.persistence.models.universilabs.repositories.InstitutionRepository;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.universilabs.services.interfaces.IProjectService;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectRequestDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectResponseDTO;
import es.jlrn.presentation.universilabs.dtos.projects.ProjectStructureDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IProjectMapper;
import es.jlrn.presentation.universilabs.mappers.interfaces.IProjectStructureMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements IProjectService {
//
    private final ProjectRepository projectRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final IProjectMapper projectMapper;
    private final IProjectStructureMapper projectStructureMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProjectResponseDTO> findAll(String searchTerm, Pageable pageable) {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            return projectRepository.findByTitleContainingIgnoreCase(searchTerm, pageable)
                    .map(projectMapper::toResponseDTO);
        }
        return this.findAll(pageable); // Llama al método de arriba
    }

    // En ProjectServiceImpl.java
    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> findAllList() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponseDTO)
                .toList(); // 👈 También aquí queda mejor así
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> findAllActiveList() {
        // Usamos el nuevo método del repositorio
        return projectRepository.findByActivoTrue().stream()
                .map(projectMapper::toResponseDTO)
                .toList(); 
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + id));
        return projectMapper.toResponseDTO(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectStructureDTO findStructureById(Long id) {
        // 1. Buscamos la entidad (lógica de negocio)
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        // 2. Delegamos TODA la transformación al mapper (limpieza)
        return projectStructureMapper.toStructureDTO(project);
    }


    @Override
    @Transactional
    public ProjectResponseDTO save(ProjectRequestDTO dto) {
        projectRepository.findByTitleAndInstitutionId(dto.getTitle(), dto.getInstitutionId())
            .ifPresent(p -> {
                throw new ResourceConflictException(
                    String.format("Ya existe un registro con el título '%s' en esta institución.", dto.getTitle()));
            });

        Project project = projectMapper.toEntity(dto);
        project.setInstitution(institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new InstitutionNotFoundException("Institución no encontrada")));
        
        project.setCreatedBy(userRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado")));

        return projectMapper.toResponseDTO(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponseDTO update(Long id, ProjectRequestDTO dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con ID: " + id));

        boolean titleChanged = !project.getTitle().equalsIgnoreCase(dto.getTitle());
        boolean instChanged = !project.getInstitution().getId().equals(dto.getInstitutionId());

        if (titleChanged || instChanged) {
            projectRepository.findByTitleAndInstitutionId(dto.getTitle(), dto.getInstitutionId())
                .ifPresent(p -> {
                    throw new ResourceConflictException("Ya existe otro registro con ese título en esta institución.");
                });
        }

        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setLevel(dto.getLevel());
        project.setActivo(dto.getActivo());

        if (instChanged) {
            project.setInstitution(institutionRepository.findById(dto.getInstitutionId())
                    .orElseThrow(() -> new InstitutionNotFoundException("Institución no encontrada")));
        }

        return projectMapper.toResponseDTO(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));
        project.setActivo(false);
        projectRepository.save(project);
    }
}