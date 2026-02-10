package es.jlrn.presentation.universilabs.services.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.enrollments.EnrollmentResponseDTO;

public interface IEnrollmentService {
//    
    EnrollmentResponseDTO enroll(EnrollmentRequestDTO dto);
    List<EnrollmentResponseDTO> getProjectsByUser(Long userId);
    List<EnrollmentResponseDTO> getUsersByProject(Long projectId);
    void unenroll(Long id);
    Page<EnrollmentResponseDTO> findAll(Pageable pageable);
    //
    public List<EnrollmentResponseDTO> getAllList();
    boolean isUserEnrolled(Long userId, Long projectId);
    //
    void unenrollByUserAndProject(Long userId, Long projectId);

}