package es.jlrn.presentation.universilabs.services.impl;

import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.persistence.models.universilabs.models.Resource;
import es.jlrn.persistence.models.universilabs.repositories.PageRepository;
import es.jlrn.persistence.models.universilabs.repositories.ResourceRepository;
import es.jlrn.presentation.universilabs.dtos.resources.ResourceDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IResourceMapper;
import es.jlrn.presentation.universilabs.services.interfaces.IResourceService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Genera el constructor para la inyección de dependencias (mejor que @Autowired)
public class ResourceServiceImpl implements IResourceService {
//
    private final ResourceRepository resourceRepository;
    private final PageRepository pageRepository;
    private final IResourceMapper resourceMapper;

    @Override
    @Transactional
    public ResourceDTO create(ResourceDTO dto) {
        // 1. Validar que la página existe
        Page page = pageRepository.findById(dto.getPageId())
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la página con ID: " + dto.getPageId()));
        
        // 2. Convertir DTO a Entidad usando el Mapper y pasarle la página encontrada
        Resource resource = resourceMapper.toEntity(dto, page);
        
        // 3. Guardar y devolver el DTO resultante
        Resource savedResource = resourceRepository.save(resource);
        return resourceMapper.toDto(savedResource);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceDTO findById(Long id) {
        return resourceRepository.findById(id)
                .map(resourceMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Recurso no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ResourceDTO> findAll(org.springframework.data.domain.Pageable pageable) {
        // Importante: El findAll del repositorio devuelve Entidades, debemos mapearlas a DTOs
        return resourceRepository.findAll(pageable)
                .map(resourceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceDTO> findByPageId(Long pageId) {
        return resourceRepository.findByPageIdOrderByOrderAsc(pageId)
                .stream()
                .map(resourceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResourceDTO update(Long id, ResourceDTO dto) {
        // 1. Verificar que el recurso existe
        Resource existingResource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar: Recurso no encontrado con ID: " + id));
        
        // 2. Actualizar campos básicos
        existingResource.setTitle(dto.getTitle());
        existingResource.setType(dto.getType());
        existingResource.setUrl(dto.getUrl());
        existingResource.setOrder(dto.getOrder());

        // 3. Opcional: Validar y actualizar la relación con la Página si el pageId cambió
        // Esto es útil si permites mover recursos entre diferentes páginas
        if (dto.getPageId() != null && !existingResource.getPage().getId().equals(dto.getPageId())) {
            Page newPage = pageRepository.findById(dto.getPageId())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontró la nueva página con ID: " + dto.getPageId()));
            existingResource.setPage(newPage);
        }
        
        // 4. Guardar cambios y retornar el DTO
        Resource updatedResource = resourceRepository.save(existingResource);
        return resourceMapper.toDto(updatedResource);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new EntityNotFoundException("No se puede eliminar: Recurso no encontrado");
        }
        resourceRepository.deleteById(id);
    }
}