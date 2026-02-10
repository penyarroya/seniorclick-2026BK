package es.jlrn.presentation.users.services.interfaces;


public interface IUserCleanupService {
//
    /**
     * Limpieza preventiva antes de crear un nuevo usuario con el mismo email.
     */
    void cleanupBeforeCreate(String email);

    /**
     * Programa la eliminación de un usuario si no verifica su email en un tiempo determinado.
     */
    void scheduleDeleteIfNotVerified(Long userId, int expiryMinutes);

    /**
     * Cancela la tarea programada de eliminación si el usuario verifica su email.
     */
    void cancelScheduledDelete(Long userId);
    
}
