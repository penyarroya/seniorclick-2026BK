package es.jlrn.presentation.universilabs.services.impl;

import es.jlrn.persistence.models.universilabs.models.Institution;
import es.jlrn.persistence.models.universilabs.repositories.InstitutionRepository;
import es.jlrn.presentation.universilabs.dtos.institutions.InstitutionDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.InstitutionMapper;
import es.jlrn.presentation.universilabs.services.interfaces.InstitutionService;
import es.jlrn.exceptions.exception.DataIntegrityViolationException;
import es.jlrn.exceptions.exception.InstitutionAlreadyExistsException;
import es.jlrn.exceptions.exception.InstitutionNotFoundException;
import es.jlrn.exceptions.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {
//
    private final InstitutionRepository repository;
    private final InstitutionMapper institutionMapper;
    
    @Override
    @Transactional(readOnly = true)
    public Page<InstitutionDTO> getAllPaginated(Pageable pageable, String global, String name, String email) {
        
        // Delegamos la creación de filtros al método estático que ya tienes
        Specification<Institution> spec = getSpecifications(global, name, email);

        // Ejecutamos la consulta y mapeamos los resultados
        return repository.findAll(spec, pageable)
                .map(institutionMapper::toDTO);
    }
    
    // ====== LIST ALL ======
    @Override
    public List<InstitutionDTO> getAll() {
        log.info("Fetching all institutions");
        return repository.findAll()
                .stream()
                .map(institutionMapper::toDTO)
                .toList();
    }

    // ====== GET BY ID ======
    @Override
    public InstitutionDTO getById(Long id) {
        log.info("Fetching institution with id {}", id);
        Institution institution = repository.findById(id)
                .orElseThrow(() -> new InstitutionNotFoundException(id));
        return institutionMapper.toDTO(institution);
    }

    @Override
    public InstitutionDTO getCurrent() {
        return repository.findFirstByOrderByIdAsc()
                .map(institutionMapper::toDTO)
                .orElse(null); // Más limpio y seguro
    }

    // ====== CREATE ======
    @Override
    @Transactional
    public InstitutionDTO create(InstitutionDTO dto) {
        log.info("Creating institution with email {}", dto.getEmail());

        // Verificar email duplicado
        if (repository.existsByEmail(dto.getEmail())) {
            throw new InstitutionAlreadyExistsException(dto.getEmail());
        }

        Institution entity = institutionMapper.toEntity(dto);
        Institution saved = repository.save(entity);

        return institutionMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public InstitutionDTO update(Long id, InstitutionDTO dto) {
        log.info("Updating institution with id {}", id);

        Institution existing = repository.findById(id)
                .orElseThrow(() -> new InstitutionNotFoundException(id));

        // Validar email si el usuario intenta cambiarlo por uno que ya existe
        if (!existing.getEmail().equalsIgnoreCase(dto.getEmail()) &&
                repository.existsByEmail(dto.getEmail())) {
            throw new InstitutionAlreadyExistsException(dto.getEmail());
        }

        // Actualizamos campos
        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setWebsite(dto.getWebsite());
        existing.setDescription(dto.getDescription());
        existing.setEmail(dto.getEmail());

        // Al usar @Transactional y save, JPA sincroniza los cambios
        return institutionMapper.toDTO(repository.save(existing));
    }

    // ====== DELETE ======
    @Override
    @Transactional
    public void delete(Long id) {
        // 1. Verificar si existe
        Institution institution = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La institución no existe."));

        // 2. Proteger si tiene proyectos
        if (institution.getProjects() != null && !institution.getProjects().isEmpty()) {
            throw new DataIntegrityViolationException(
                "No se puede eliminar la institución '" + institution.getName() + 
                "' porque tiene proyectos asociados. Elimine o mueva los proyectos primero."
            );
        }

        // 3. Si está libre de proyectos, borrar
        repository.delete(institution);
    }

    // Clase de utilidad o dentro de InstitutionServiceImpl
    public static Specification<Institution> getSpecifications(String global, String name, String email) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // FILTRO GLOBAL: Busca el término en varias columnas a la vez (OR)
            if (global != null && !global.isEmpty()) {
                String pattern = "%" + global.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            // FILTROS ESPECÍFICOS: (Si los necesitas por separado)
            if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (email != null) predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

}
