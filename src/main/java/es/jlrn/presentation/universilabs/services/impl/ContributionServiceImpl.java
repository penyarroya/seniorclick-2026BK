package es.jlrn.presentation.universilabs.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.Contribution;
import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.persistence.models.universilabs.repositories.ContributionRepository;
import es.jlrn.persistence.models.universilabs.repositories.PageRepository;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionRequestDTO;
import es.jlrn.presentation.universilabs.dtos.contributions.ContributionResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.IContributionMapper;
import es.jlrn.presentation.universilabs.services.interfaces.IContributionService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContributionServiceImpl implements IContributionService {

    private final ContributionRepository contributionRepository;
    private final PageRepository pageRepository;
    private final UserRepository userRepository;
    private final IContributionMapper contributionMapper;

    // --- NUEVO MÉTODO PARA EL MANTENIMIENTO ---
    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponseDTO> list() {
        // Obtenemos todas las contribuciones ordenadas por fecha
        return contributionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(contributionMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponseDTO> getByPage(Long pageId) {
        if (!pageRepository.existsById(pageId)) throw new ResourceNotFoundException("Página no encontrada");
        return contributionRepository.findByPageIdOrderByCreatedAtDesc(pageId).stream()
                .map(contributionMapper::toResponseDTO)
                .toList();
    }

    // --- NUEVO MÉTODO PARA "MIS APORTACIONES" ---
    @Override
    @Transactional(readOnly = true)
    public List<ContributionResponseDTO> getByUser(Long userId) {
        // Verificamos que el usuario existe antes de buscar
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }

        // Buscamos sus aportaciones usando el repositorio ordenado
        return contributionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(contributionMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ContributionResponseDTO save(ContributionRequestDTO dto) {
        Page page = pageRepository.findById(dto.pageId())
                .orElseThrow(() -> new ResourceNotFoundException("Página no encontrada"));
        UserEntity user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Contribution contribution = contributionMapper.toEntity(dto);
        contribution.setPage(page);
        contribution.setUser(user);

        return contributionMapper.toResponseDTO(contributionRepository.save(contribution));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!contributionRepository.existsById(id)) throw new ResourceNotFoundException("Contribución no encontrada");
        contributionRepository.deleteById(id);
    }
}