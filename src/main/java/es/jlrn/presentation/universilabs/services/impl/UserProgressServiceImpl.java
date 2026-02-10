package es.jlrn.presentation.universilabs.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.Enrollment;
import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.persistence.models.universilabs.models.UserProgress;
import es.jlrn.persistence.models.universilabs.repositories.EnrollmentRepository;
import es.jlrn.persistence.models.universilabs.repositories.PageRepository;
import es.jlrn.persistence.models.universilabs.repositories.UserProgressRepository;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressRequestDTO;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IUserProgressMapper;
import es.jlrn.presentation.universilabs.services.interfaces.IUserProgressService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProgressServiceImpl implements IUserProgressService {
//
    private final UserProgressRepository progressRepository;
    private final PageRepository pageRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final IUserProgressMapper progressMapper;

    private static final List<String> MOTIVATION_MESSAGES = List.of(
        "¡Muy bien hecho! Vamos a la siguiente lección.",
        "¡Excelente! Estás aprendiendo muy rápido.",
        "¡Eres un genio! Cada paso cuenta.",
        "¡Qué alegría! Has completado una lección más.",
        "¡Fantástico! Sigue así, lo estás haciendo genial."
    );

    @Override
    @Transactional
    public UserProgressResponseDTO updateProgress(UserProgressRequestDTO dto) {
        Page page = findPageOrThrow(dto.pageId());
        Long projectId = extractProjectId(page);
        Enrollment enrollment = findEnrollmentOrThrow(dto.userId(), projectId);

        UserProgress progress = progressRepository.findByEnrollmentAndPage_Id(enrollment, dto.pageId())
                .orElseGet(() -> UserProgress.builder()
                        .enrollment(enrollment)
                        .page(page)
                        .status("STARTED")
                        .timeSpentSeconds(0)
                        .build());

        progress.setStatus(dto.status());
        if (dto.timeSpentSeconds() != null) {
            progress.setTimeSpentSeconds(dto.timeSpentSeconds());
        }
        progress.setLastAccess(LocalDateTime.now());
        UserProgress saved = progressRepository.save(progress);

        // Retornamos el DTO con los campos de navegación en null/false porque es solo una actualización
        return new UserProgressResponseDTO(
            saved.getId(), saved.getStatus(), saved.getTimeSpentSeconds(),
            saved.getLastAccess(), dto.userId(), dto.pageId(),
            null, null, false
        );
    }

    @Override
    @Transactional
    public UserProgressResponseDTO completePage(Long userId, Long projectId, Long pageId) {
        Enrollment enrollment = findEnrollmentOrThrow(userId, projectId);

        UserProgress progress = progressRepository.findByEnrollmentAndPage_Id(enrollment, pageId)
                .orElseGet(() -> UserProgress.builder()
                        .enrollment(enrollment)
                        .page(findPageOrThrow(pageId))
                        .timeSpentSeconds(0) // Inicializamos para evitar nulos
                        .build());

        progress.setStatus("COMPLETED");
        progress.setLastAccess(LocalDateTime.now());

        // Lógica de navegación y motivación
        Optional<Page> nextPage = findNextPage(projectId, pageId);
        String randomMessage = nextPage.isPresent() 
            ? MOTIVATION_MESSAGES.get(new Random().nextInt(MOTIVATION_MESSAGES.size()))
            : "¡Enhorabuena! Has completado el curso.";

        // PERSISTENCIA: Guardamos el mensaje en la entidad para que no se pierda
        progress.setMotivationMessage(randomMessage); 
        
        UserProgress saved = progressRepository.save(progress);

        // Retornamos usando el campo ya guardado en 'saved'
        return new UserProgressResponseDTO(
            saved.getId(),
            saved.getStatus(),
            saved.getTimeSpentSeconds(),
            saved.getLastAccess(),
            userId,
            pageId,
            saved.getMotivationMessage(), // <--- Obtenido de la DB
            nextPage.map(Page::getId).orElse(null),
            nextPage.isEmpty()
        );
    }

    @Override
    @Transactional
    public UserProgressResponseDTO updateMotivationMessage(Long userId, Long pageId, String newMessage) {
        // Buscamos el progreso existente
        UserProgress progress = progressRepository.findByEnrollment_User_IdAndPage_Id(userId, pageId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe registro de progreso para este usuario y página."));

        // Actualizamos solo el mensaje
        progress.setMotivationMessage(newMessage);
        progress.setLastAccess(LocalDateTime.now()); // Opcional: marcar como última edición
        
        UserProgress saved = progressRepository.save(progress);

        // Retornamos el DTO (los campos de navegación pueden ir en null ya que es una edición técnica)
        return new UserProgressResponseDTO(
            saved.getId(),
            saved.getStatus(),
            saved.getTimeSpentSeconds(),
            saved.getLastAccess(),
            userId,
            pageId,
            saved.getMotivationMessage(),
            null, 
            false
        );
    }


    @Override
    @Transactional(readOnly = true)
    public Double getCompletionPercentage(Long userId, Long projectId) {
        long totalPages = pageRepository.countPagesByProjectId(projectId);
        if (totalPages == 0) return 0.0;

        long completedPages = progressRepository.countCompletedPagesByUserIdAndProjectId(userId, projectId);
        double percentage = (double) completedPages / totalPages * 100;
        return Math.round(percentage * 10.0) / 10.0;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProgressResponseDTO resumeCourse(Long userId, Long projectId) {
        Enrollment enrollment = findEnrollmentOrThrow(userId, projectId);
        UserProgress lastProgress = progressRepository.findFirstByEnrollmentOrderByLastAccessDesc(enrollment)
                .orElseThrow(() -> new ResourceNotFoundException("Aún no tienes progreso en este curso."));

        return progressMapper.toResponseDTO(lastProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isProjectCompleted(Long userId, Long projectId) {
        return getCompletionPercentage(userId, projectId) >= 100.0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProgressResponseDTO> getAllProgressByUser(Long userId) {
        if (!userRepository.existsById(userId)) throw new ResourceNotFoundException("Usuario no encontrado");
        return progressRepository.findByEnrollment_User_Id(userId).stream()
                .map(progressMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserProgressResponseDTO getProgress(Long userId, Long pageId) {
        return progressRepository.findByEnrollment_User_IdAndPage_Id(userId, pageId)
                .map(progressMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No hay registro de progreso."));
    }

    // --- MÉTODOS PRIVADOS DE APOYO ---

    private Optional<Page> findNextPage(Long projectId, Long currentPageId) {
        List<Page> allPages = pageRepository.findAllPagesByProjectOrdered(projectId);
        for (int i = 0; i < allPages.size(); i++) {
            if (allPages.get(i).getId().equals(currentPageId)) {
                if (i + 1 < allPages.size()) return Optional.of(allPages.get(i + 1));
            }
        }
        return Optional.empty();
    }

    private Page findPageOrThrow(Long pageId) {
        return pageRepository.findById(pageId)
                .orElseThrow(() -> new ResourceNotFoundException("La página " + pageId + " no existe."));
    }

    private Enrollment findEnrollmentOrThrow(Long userId, Long projectId) {
        return enrollmentRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no está inscrito en el proyecto."));
    }

    private Long extractProjectId(Page page) {
        return page.getSubtopic().getTopic().getCollection().getProject().getId();
    }

}