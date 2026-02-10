package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;

import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressRequestDTO;
import es.jlrn.presentation.universilabs.dtos.usersprogress.UserProgressResponseDTO;

public interface IUserProgressService {
//
    /**
     * Registra o actualiza el tiempo y estado (STARTED/COMPLETED) de una página.
     */
    UserProgressResponseDTO updateProgress(UserProgressRequestDTO dto);

    /**
     * Marca una página como completada específicamente, calculando motivación y navegación.
     */
    UserProgressResponseDTO completePage(Long userId, Long projectId, Long pageId);

    /**
     * Obtiene el progreso de un usuario en una página específica.
     */
    UserProgressResponseDTO getProgress(Long userId, Long pageId);

    /**
     * Obtiene todo el progreso acumulado de un usuario en la plataforma.
     */
    List<UserProgressResponseDTO> getAllProgressByUser(Long userId);

    /**
     * Encuentra la última página visitada para que el usuario retome donde lo dejó.
     */
    UserProgressResponseDTO resumeCourse(Long userId, Long projectId);

    /**
     * Calcula el porcentaje (0.0 a 100.0) de progreso en un proyecto.
     */
    Double getCompletionPercentage(Long userId, Long projectId);

    /**
     * Verifica si todas las páginas de un proyecto han sido marcadas como COMPLETED.
     */
    boolean isProjectCompleted(Long userId, Long projectId);

    UserProgressResponseDTO updateMotivationMessage(Long userId, Long pageId, String newMessage);

}