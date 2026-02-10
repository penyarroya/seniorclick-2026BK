package es.jlrn.presentation.universilabs.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.exceptions.exception.ResourceNotFoundException;
import es.jlrn.persistence.models.universilabs.models.Comment;
import es.jlrn.persistence.models.universilabs.models.Page;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.universilabs.repositories.CommentRepository;
import es.jlrn.persistence.models.universilabs.repositories.PageRepository;
import es.jlrn.persistence.models.users.repositories.UserRepository;
import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
import es.jlrn.presentation.universilabs.mappers.interfaces.ICommentMapper;
import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
//
    private final CommentRepository commentRepository;
    private final PageRepository pageRepository;
    private final UserRepository userRepository;
    private final ICommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> findByPageId(Long pageId) {
        // Validar que la página existe
        if (!pageRepository.existsById(pageId)) {
            throw new ResourceNotFoundException("No se pueden listar comentarios: Página no encontrada con ID: " + pageId);
        }

        return commentRepository.findByPageIdOrderByCreatedAtDesc(pageId).stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> findAll() {
        // Cambiamos findAll() por el nuevo método ordenado
        return commentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDTO save(CommentRequestDTO dto) {
        // 1. Validar que la página existe
        Page page = pageRepository.findById(dto.pageId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede comentar: La página no existe."));

        // 2. Validar que el usuario existe
        UserEntity user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede comentar: El usuario no existe."));

        // 3. Crear entidad, asignar relaciones y guardar
        Comment comment = commentMapper.toEntity(dto);
        comment.setPage(page);
        comment.setUser(user);

        return commentMapper.toResponseDTO(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: El comentario no existe.");
        }
        commentRepository.deleteById(id);
    }
}