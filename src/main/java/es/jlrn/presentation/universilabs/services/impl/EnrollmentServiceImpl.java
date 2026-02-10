package es.jlrn.presentation.universilabs.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.Enrollment;
import es.jlrn.persistence.models.universilabs.models.Project;
import es.jlrn.persistence.models.universilabs.repositories.EnrollmentRepository;
import es.jlrn.persistence.models.universilabs.repositories.ProjectRepository;
import es.jlrn.persistence.models.universilabs.repositories.UserProgressRepository;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IEnrollmentMapper;
import es.jlrn.presentation.universilabs.services.interfaces.IEnrollmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements IEnrollmentService {
//
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final IEnrollmentMapper enrollmentMapper; // Ya lo tienes inyectado, ¡perfecto!
    private final UserProgressRepository userProgressRepository;

    // Dentro de tu clase EnrollmentServiceImpl
    // @Override
    // @Transactional(readOnly = true)
    // public Page<EnrollmentResponseDTO> findAll(Pageable pageable) {
    //     return enrollmentRepository.findAll(pageable)
    //             .map(enrollmentMapper::toResponseDTO);
    // }

    // Opcional: Si quieres un método que traiga sin paginar para selectores
    // @Override
    // @Transactional(readOnly = true)
    // public List<EnrollmentResponseDTO> getAllList() {
    //     return enrollmentRepository.findAll().stream()
    //             .map(enrollmentMapper::toResponseDTO)
    //             .toList();
    // }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponseDTO> findAll(Pageable pageable) {
        return enrollmentRepository.findAll(pageable)
                .map(this::mapToDetailedResponseDTO); // Usamos un método privado para no repetir código
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getAllList() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapToDetailedResponseDTO)
                .toList();
    }

    /**
     * Método privado de apoyo para centralizar la creación del DTO con progreso
     */
    private EnrollmentResponseDTO mapToDetailedResponseDTO(Enrollment enrollment) {
        EnrollmentResponseDTO base = enrollmentMapper.toResponseDTO(enrollment);

        Integer lastPage = userProgressRepository
            .findFirstByEnrollmentIdOrderByLastAccessDesc(enrollment.getId())
            .map(progress -> progress.getPage().getId().intValue())
            .orElse(1);

        return new EnrollmentResponseDTO(
            base.id(),
            base.userId(),
            base.userName(),
            base.projectId(),
            base.projectTitle(),
            base.roleInCourse(),
            base.createdAt(),
            lastPage,
            enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserEnrolled(Long userId, Long projectId) {
        // Usamos el método que ya existe en tu repositorio
        return enrollmentRepository.existsByUserIdAndProjectId(userId, projectId);
    }

    @Override
    @Transactional
    public EnrollmentResponseDTO enroll(EnrollmentRequestDTO dto) {
        // 1. Evitar duplicados
        if (enrollmentRepository.existsByUserIdAndProjectId(dto.userId(), dto.projectId())) {
            throw new RuntimeException("El usuario ya está inscrito en este proyecto");
        }

        // 2. Validar entidades
        UserEntity user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado"));

        // 3. Crear y guardar
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .project(project)
                .roleInCourse(dto.roleInCourse())
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        
        // CAMBIO: Usamos el mapper en lugar de crear el objeto manualmente
        return enrollmentMapper.toResponseDTO(saved);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public List<EnrollmentResponseDTO> getProjectsByUser(Long userId) {
    //     return enrollmentRepository.findByUserId(userId).stream()
    //             // CAMBIO: Referencia al método del mapper inyectado
    //             .map(enrollmentMapper::toResponseDTO) 
    //             .toList();
    // }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getProjectsByUser(Long userId) {
        return enrollmentRepository.findByUserId(userId).stream()
                .map(enrollment -> {
                    // 1. Mapeo inicial (campos básicos)
                    EnrollmentResponseDTO base = enrollmentMapper.toResponseDTO(enrollment);

                    // 2. Buscamos la última página desde la tabla user_progress
                    Integer lastPage = userProgressRepository
                        .findFirstByEnrollmentIdOrderByLastAccessDesc(enrollment.getId())
                        .map(progress -> progress.getPage().getId().intValue())
                        .orElse(1); // Si es nuevo, empezamos en la 1

                    // 3. Retornamos una NUEVA instancia del Record (constructor completo)
                    // Esto elimina el error de inferencia <R>
                    return new EnrollmentResponseDTO(
                        base.id(),
                        base.userId(),
                        base.userName(),
                        base.projectId(),
                        base.projectTitle(),
                        base.roleInCourse(),
                        base.createdAt(),
                        lastPage, // <-- Ahora sí llega el dato real
                        enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0
                    );
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getUsersByProject(Long projectId) {
        return enrollmentRepository.findByProjectId(projectId).stream()
                // CAMBIO: Referencia al método del mapper inyectado
                .map(enrollmentMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void unenroll(Long id) {
        // 1. Verificamos si existe
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + id));
        
        // 2. Al borrar el objeto enrollment, Hibernate detectará la cascada
        // y borrará automáticamente todos los registros asociados en 'user_progress'
        enrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional
    public void unenrollByUserAndProject(Long userId, Long projectId) {
        // 1. Buscamos la inscripción activa
        Enrollment enrollment = enrollmentRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No existe inscripción para el usuario " + userId + " en el proyecto " + projectId));

        // 2. Ejecutamos el borrado. 
        // Gracias a 'cascade = CascadeType.ALL', esto borrará automáticamente 
        // los registros hijos en la tabla 'user_progress'
        enrollmentRepository.delete(enrollment);
    }

    // @Override
    // @Transactional
    // public void unenroll(Long id) {
    //     if (!enrollmentRepository.existsById(id)) {
    //         throw new ResourceNotFoundException("Inscripción no encontrada");
    //     }
    //     enrollmentRepository.deleteById(id);
    // }
}