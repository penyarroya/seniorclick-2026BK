package es.jlrn.presentation.universilabs.services.impl;

import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.persistence.models.universilabs.models.Subtopic;
import es.jlrn.persistence.models.universilabs.models.UserProgress;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.universilabs.repositories.PageRepository;
import es.jlrn.persistence.models.universilabs.repositories.ResourceRepository;
import es.jlrn.persistence.models.universilabs.repositories.SubtopicRepository;
import es.jlrn.persistence.models.universilabs.repositories.UserProgressRepository;
import es.jlrn.persistence.models.users.repositories.UserRepository; // Ajusta según tu proyecto
import es.jlrn.presentation.universilabs.dtos.pages.PageDetailDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageRequestDTO;
import es.jlrn.presentation.universilabs.dtos.pages.PageResponseDTO;
import es.jlrn.presentation.universilabs.dtos.resources.ResourceDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IPageMapper;
import es.jlrn.presentation.universilabs.mappers.interfaces.IResourceMapper;
import es.jlrn.presentation.universilabs.services.interfaces.IPageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageServiceImpl implements IPageService {
//
    private final PageRepository pageRepository;
    private final SubtopicRepository subtopicRepository;
    private final UserRepository userRepository; 
    private final IPageMapper pageMapper;
    private final IResourceMapper resourceMapper;

    private final ResourceRepository resourceRepository; // <--- AGREGA ESTA LÍNEA
    private final UserProgressRepository progressRepository; // <--- AGREGA ESTA TAMBIÉN
    

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<PageResponseDTO> findAll(org.springframework.data.domain.Pageable pageable) {
        return pageRepository.findAll(pageable)
                .map(pageMapper::toResponseDTO); // El mapper ya mete los títulos dentro del constructor del record
    }

    @Override
    @Transactional(readOnly = true)
    public List<PageResponseDTO> findBySubtopicId(Long subtopicId) {
        if (!subtopicRepository.existsById(subtopicId)) {
            throw new ResourceNotFoundException("No se pueden listar páginas: Subtema no encontrado con ID: " + subtopicId);
        }
        return pageRepository.findBySubtopicId(subtopicId).stream()
                .map(pageMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO findById(Long id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Página no encontrada con ID: " + id));
        return pageMapper.toResponseDTO(page);
    }

    // @Override
    // @Transactional
    // public PageResponseDTO save(PageRequestDTO dto) {
    //     // 1. Validar que el Subtema existe
    //     Subtopic subtopic = subtopicRepository.findById(dto.subtopicId())
    //             .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la página: El subtema no existe."));

    //     // 2. Validar que el Autor existe
    //     UserEntity author = userRepository.findById(dto.authorId())
    //             .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la página: El autor no existe."));

    //     // 3. Crear entidad y asignar relaciones
    //     Page page = pageMapper.toEntity(dto);
    //     page.setSubtopic(subtopic);
    //     page.setAuthor(author);

    //     return pageMapper.toResponseDTO(pageRepository.save(page));
    // }

    // @Override
    // @Transactional
    // public PageResponseDTO update(Long id, PageRequestDTO dto) {
    //     // 1. Buscar la página existente
    //     Page page = pageRepository.findById(id)
    //             .orElseThrow(() -> new ResourceNotFoundException("Página no encontrada para actualizar."));

    //     // 2. Lógica del Subtema: Validar si ha cambiado
    //     if (!page.getSubtopic().getId().equals(dto.subtopicId())) {
    //         Subtopic newSubtopic = subtopicRepository.findById(dto.subtopicId())
    //                 .orElseThrow(() -> new ResourceNotFoundException("El subtema destino no existe."));
    //         page.setSubtopic(newSubtopic);
    //     }

    //     // 3. Actualizar campos básicos (IMPORTANTE: incluir el título)
    //     page.setTitle(dto.title()); 
    //     page.setContent(dto.content());

    //     // 4. Guardar y retornar el DTO actualizado
    //     return pageMapper.toResponseDTO(pageRepository.save(page));
    // }

    @Override
    @Transactional
    public PageResponseDTO save(PageRequestDTO dto) {
        Subtopic subtopic = subtopicRepository.findById(dto.subtopicId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la página: El subtema no existe."));

        UserEntity author = userRepository.findById(dto.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la página: El autor no existe."));

        Page page = pageMapper.toEntity(dto);
        // El mapper ya debería traer el format si lo actualizaste, 
        // pero por seguridad lo asignamos si el mapper no lo hace:
        page.setFormat(dto.format()); 
        page.setSubtopic(subtopic);
        page.setAuthor(author);

        return pageMapper.toResponseDTO(pageRepository.save(page));
    }

    @Override
    @Transactional
    public PageResponseDTO update(Long id, PageRequestDTO dto) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Página no encontrada para actualizar."));

        if (!page.getSubtopic().getId().equals(dto.subtopicId())) {
            Subtopic newSubtopic = subtopicRepository.findById(dto.subtopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("El subtema destino no existe."));
            page.setSubtopic(newSubtopic);
        }

        // ACTUALIZACIÓN DE FORMATO Y CONTENIDO
        page.setTitle(dto.title()); 
        page.setContent(dto.content());
        page.setFormat(dto.format()); // <--- Crucial para permitir cambiar de Texto a HTML, etc.

        return pageMapper.toResponseDTO(pageRepository.save(page));
    }

    // @Transactional(readOnly = true)
    // public PageDetailDTO getPageDetailForUser(Long pageId, Long userId) {
    //     // 1. Obtener la página
    //     Page page = pageRepository.findById(pageId)
    //         .orElseThrow(() -> new EntityNotFoundException("Página no encontrada"));

    //     // 2. Obtener sus recursos
    //     List<ResourceDTO> resources = resourceRepository.findByPageIdOrderByOrderAsc(pageId)
    //         .stream()
    //         .map(resourceMapper::toDto)
    //         .collect(Collectors.toList());

    //     // 3. Buscar el progreso (usando la relación con Enrollment)
    //     Optional<UserProgress> progress = progressRepository.findByEnrollment_User_IdAndPage_Id(userId, pageId);

    //     // 4. LÓGICA NUEVA: ¿Es la última página del proyecto?
    //     Long projectId = page.getSubtopic().getTopic().getCollection().getProject().getId();
    //     List<Page> allPages = pageRepository.findAllPagesByProjectOrdered(projectId);
        
    //     boolean isLastPage = false;
    //     if (!allPages.isEmpty()) {
    //         // Comparamos el ID de la página actual con el ID de la última página de la lista
    //         isLastPage = allPages.get(allPages.size() - 1).getId().equals(pageId);
    //     }

    //     // 5. Construir el Super DTO (Asegúrate de que PageDetailDTO tenga el campo isLastPage)
    //     return PageDetailDTO.builder()
    //             .id(page.getId())
    //             .title(page.getTitle())
    //             .content(page.getContent())
    //             .resources(resources)
    //             .userStatus(progress.map(UserProgress::getStatus).orElse("NOT_STARTED"))
    //             .timeSpentSeconds(progress.map(UserProgress::getTimeSpentSeconds).orElse(0))
    //             .isLastPage(isLastPage) // <--- Pasamos este nuevo dato
    //             .build();
    // }

    @Transactional(readOnly = true)
    public PageDetailDTO getPageDetailForUser(Long pageId, Long userId) {
        Page page = pageRepository.findById(pageId)
            .orElseThrow(() -> new EntityNotFoundException("Página no encontrada"));

        List<ResourceDTO> resources = resourceRepository.findByPageIdOrderByOrderAsc(pageId)
            .stream()
            .map(resourceMapper::toDto)
            .collect(Collectors.toList());

        Optional<UserProgress> progress = progressRepository.findByEnrollment_User_IdAndPage_Id(userId, pageId);

        Long projectId = page.getSubtopic().getTopic().getCollection().getProject().getId();
        List<Page> allPages = pageRepository.findAllPagesByProjectOrdered(projectId);
        
        boolean isLastPage = false;
        if (!allPages.isEmpty()) {
            isLastPage = allPages.get(allPages.size() - 1).getId().equals(pageId);
        }

        return PageDetailDTO.builder()
                .id(page.getId())
                .title(page.getTitle())
                .format(page.getFormat()) // <--- NUEVO: El frontend necesita esto
                .content(page.getContent())
                .resources(resources)
                .userStatus(progress.map(UserProgress::getStatus).orElse("NOT_STARTED"))
                .timeSpentSeconds(progress.map(UserProgress::getTimeSpentSeconds).orElse(0))
                .isLastPage(isLastPage)
                .build();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!pageRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Página no encontrada.");
        }
        pageRepository.deleteById(id);
    }
}