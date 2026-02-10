package es.jlrn.presentation.users.services.impl;

import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.users.services.interfaces.IUserCleanupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class UserCleanupServiceImpl implements IUserCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(UserCleanupServiceImpl.class);

    private final UserRepository userRepository;
    private final TaskScheduler taskScheduler;

    private final ConcurrentHashMap<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public UserCleanupServiceImpl(UserRepository userRepository, TaskScheduler taskScheduler) {
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
    }

    /**
     * Limpieza preventiva antes de crear un nuevo usuario con el mismo email.
     */
    @Override
    @Transactional
    public void cleanupBeforeCreate(String email) {
        LocalDateTime now = LocalDateTime.now();
        int deleted = userRepository.deleteUnverifiedUsersByEmail(email, now);
        if (deleted > 0) {
            logger.info("Usuarios antiguos no verificados con email '{}' eliminados: {}", email, deleted);
        }
    }

    /**
     * Limpieza automática programada cada 5 minutos.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // cada 5 minutos
    @Transactional
    public void cleanupScheduler() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = userRepository.deleteExpiredUnverifiedUsers(now);
        if (deleted > 0) {
            logger.info("Usuarios no verificados expirados eliminados: {}", deleted);
        }
    }

    /**
     * Programa la eliminación de un usuario si no verifica su email en un tiempo determinado.
     */
    @Override
    public void scheduleDeleteIfNotVerified(Long userId, int expiryMinutes) {
        Runnable task = () -> deleteUserIfNotVerified(userId);

        ScheduledFuture<?> future = taskScheduler.schedule(
            task,
            LocalDateTime.now().plusMinutes(expiryMinutes)
                         .atZone(taskScheduler.getClock().getZone())
                         .toInstant()
        );

        scheduledTasks.put(userId, future);
    }

    /**
     * Cancela la tarea programada si el usuario verifica su email.
     */
    @Override
    public void cancelScheduledDelete(Long userId) {
        ScheduledFuture<?> future = scheduledTasks.remove(userId);
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * Elimina un usuario si no ha verificado su email usando DELETE directo del repositorio.
     */
    @Transactional
    public void deleteUserIfNotVerified(Long userId) {
        int deleted = userRepository.deleteIfNotVerified(userId);
        if (deleted > 0) {
            logger.info("Usuario no verificado eliminado: ID {}", userId);
        } else {
            logger.info("Usuario ya verificado o no encontrado: ID {}", userId);
        }

        scheduledTasks.remove(userId);
    }
}
