package es.jlrn.presentation.universilabs.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import es.jlrn.exceptions.exception.ResourceConflictException;
import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.CollectionEntity;
import es.jlrn.persistence.models.universilabs.models.Project;
import es.jlrn.persistence.models.universilabs.repositories.CollectionRepository;
import es.jlrn.persistence.models.universilabs.repositories.ProjectRepository;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.collections.CollectionResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ICollectionMapper;
import es.jlrn.presentation.universilabs.services.interfaces.ICollectionService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements ICollectionService {
//
    private final CollectionRepository collectionRepository;
    private final ProjectRepository projectRepository;
    private final ICollectionMapper collectionMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CollectionResponseDTO> findAllPaginated(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CollectionEntity> entities;
        
        if (search != null && !search.isEmpty()) {
            entities = collectionRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            entities = collectionRepository.findAll(pageable);
        }
        
        return entities.map(collectionMapper::toResponseDTO);
    }

    // En CollectionServiceImpl.java

    @Override
    @Transactional(readOnly = true)
    public List<CollectionResponseDTO> findByProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("No se pueden listar colecciones: Proyecto no encontrado con ID: " + projectId);
        }
        
        return collectionRepository.findByProjectId(projectId).stream()
                .map(collectionMapper::toResponseDTO)
                .toList(); // 👈 SUSTITUYE AQUÍ .collect(Collectors.toList()) por .toList()
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponseDTO findById(Long id) {
        CollectionEntity collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada con ID: " + id));
        return collectionMapper.toResponseDTO(collection);
    }

    @Override
    @Transactional
    public CollectionResponseDTO save(CollectionRequestDTO dto) {
        // 1. Validar que el proyecto existe y está ACTIVO
        // Esto garantiza que no se asocien datos a proyectos "borrados" o archivados
        Project project = projectRepository.findByIdAndActivoTrue(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No se puede crear la colección: El proyecto no existe o está inactivo."));

        // 2. Validar que no exista otra colección con el mismo nombre en este proyecto
        // Importante para mantener el orden y evitar confusión en el usuario
        collectionRepository.findByNameAndProjectId(dto.getName(), dto.getProjectId())
                .ifPresent(c -> {
                    throw new ResourceConflictException(
                        String.format("Ya existe una colección llamada '%s' en este proyecto.", dto.getName()));
                });

        // 3. Crear entidad, asignar la relación y guardar
        CollectionEntity collection = collectionMapper.toEntity(dto);
        collection.setProject(project); // Establece la FK (Foreign Key)

        return collectionMapper.toResponseDTO(collectionRepository.save(collection));
    }

    @Override
    @Transactional
    public CollectionResponseDTO update(Long id, CollectionRequestDTO dto) {
        // 1. Buscar la colección existente
        CollectionEntity collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colección no encontrada para actualizar."));

        // 2. Si cambia el nombre o el proyecto, validar duplicados
        boolean nameChanged = !collection.getName().equalsIgnoreCase(dto.getName());
        boolean projectChanged = !collection.getProject().getId().equals(dto.getProjectId());

        if (nameChanged || projectChanged) {
            collectionRepository.findByNameAndProjectId(dto.getName(), dto.getProjectId())
                    .ifPresent(c -> {
                        throw new ResourceConflictException("Conflicto: El nombre ya está en uso en el proyecto destino.");
                    });
            
            // Si el proyecto cambió, validar que el nuevo proyecto exista y esté activo
            if (projectChanged) {
                Project newProject = projectRepository.findByIdAndActivoTrue(dto.getProjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("El proyecto destino no existe o está inactivo."));
                collection.setProject(newProject);
            }
        }

        collection.setName(dto.getName());
        return collectionMapper.toResponseDTO(collectionRepository.save(collection));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!collectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Colección no encontrada.");
        }
        collectionRepository.deleteById(id);
    }
}
