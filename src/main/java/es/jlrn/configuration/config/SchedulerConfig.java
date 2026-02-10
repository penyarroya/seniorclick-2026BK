package es.jlrn.configuration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuración para habilitar tareas programadas en la aplicación.
 * Proporciona un TaskScheduler que se usará en servicios como UserCleanupServiceImpl.
 */
@Configuration
@EnableScheduling // Habilita @Scheduled y otras tareas programadas
public class SchedulerConfig {

    /**
     * Bean de TaskScheduler para ejecutar tareas programadas de forma concurrente.
     * @return TaskScheduler configurado con un pool de hilos.
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10); // Número de tareas simultáneas
        scheduler.setThreadNamePrefix("user-cleanup-"); // Prefijo para los hilos
        scheduler.initialize();
        return scheduler;
    }
}
