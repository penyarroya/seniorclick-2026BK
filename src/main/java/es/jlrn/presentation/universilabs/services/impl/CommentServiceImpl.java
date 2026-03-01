// package es.jlrn.presentation.universilabs.services.impl;

// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import es.jlrn.exceptions.exception.ResourceNotFoundException;
// import es.jlrn.persistence.models.universilabs.models.Comment;
// import es.jlrn.persistence.models.universilabs.models.Page;
// import es.jlrn.persistence.models.users.models.UserEntity;
// import es.jlrn.persistence.models.universilabs.repositories.CommentRepository;
// import es.jlrn.persistence.models.universilabs.repositories.PageRepository;
// import es.jlrn.persistence.models.users.repositories.UserRepository;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
// import es.jlrn.presentation.universilabs.mappers.interfaces.ICommentMapper;
// import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CommentServiceImpl implements ICommentService {
// //
//     private final CommentRepository commentRepository;
//     private final PageRepository pageRepository;
//     private final UserRepository userRepository;
//     private final ICommentMapper commentMapper;

//     @Override
//     @Transactional(readOnly = true)
//     public List<CommentResponseDTO> findByPageId(Long pageId) {
//         // Validar que la página existe
//         if (!pageRepository.existsById(pageId)) {
//             throw new ResourceNotFoundException("No se pueden listar comentarios: Página no encontrada con ID: " + pageId);
//         }

//         return commentRepository.findByPageIdOrderByCreatedAtDesc(pageId).stream()
//                 .map(commentMapper::toResponseDTO)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<CommentResponseDTO> findAll() {
//         // Cambiamos findAll() por el nuevo método ordenado
//         return commentRepository.findAllByOrderByCreatedAtDesc().stream()
//                 .map(commentMapper::toResponseDTO)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     @Transactional
//     public CommentResponseDTO save(CommentRequestDTO dto) {
//         // 1. Validar que la página existe
//         Page page = pageRepository.findById(dto.pageId())
//                 .orElseThrow(() -> new ResourceNotFoundException("No se puede comentar: La página no existe."));

//         // 2. Validar que el usuario existe
//         UserEntity user = userRepository.findById(dto.userId())
//                 .orElseThrow(() -> new ResourceNotFoundException("No se puede comentar: El usuario no existe."));

//         // 3. Crear entidad, asignar relaciones y guardar
//         Comment comment = commentMapper.toEntity(dto);
//         comment.setPage(page);
//         comment.setUser(user);

//         return commentMapper.toResponseDTO(commentRepository.save(comment));
//     }

//     @Override
//     @Transactional
//     public void delete(Long id) {
//         if (!commentRepository.existsById(id)) {
//             throw new ResourceNotFoundException("No se puede eliminar: El comentario no existe.");
//         }
//         commentRepository.deleteById(id);
//     }
// }


// package es.jlrn.presentation.universilabs.services.impl;

// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import es.jlrn.exceptions.exception.ResourceNotFoundException;
// import es.jlrn.persistence.models.universilabs.models.Comment;
// import es.jlrn.persistence.models.universilabs.models.Page;
// import es.jlrn.persistence.models.users.models.UserEntity;
// import es.jlrn.persistence.models.universilabs.repositories.CommentRepository;
// import es.jlrn.persistence.models.universilabs.repositories.PageRepository;
// import es.jlrn.persistence.models.users.repositories.UserRepository;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentRequestDTO;
// import es.jlrn.presentation.universilabs.dtos.comments.CommentResponseDTO;
// import es.jlrn.presentation.universilabs.mappers.interfaces.ICommentMapper;
// import es.jlrn.presentation.universilabs.services.IA.AIService;
// import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CommentServiceImpl implements ICommentService {
// //
//     private final CommentRepository commentRepository;
//     private final PageRepository pageRepository;
//     private final UserRepository userRepository;
//     private final ICommentMapper commentMapper;
//     private final AIService aiService;

//     @Override
//     @Transactional(readOnly = true)
//     public List<CommentResponseDTO> findByPageId(Long pageId) {
//         if (!pageRepository.existsById(pageId)) {
//             throw new ResourceNotFoundException("Página no encontrada con ID: " + pageId);
//         }

//         // CAMBIO CLAVE: Usamos el método que trae solo los hilos principales (raíces)
//         // El mapper recursivo se encargará de traer las respuestas dentro de cada padre.
//         return commentRepository.findByPageIdAndParentIsNullOrderByCreatedAtDesc(pageId).stream()
//                 .map(commentMapper::toResponseDTO)
//                 .collect(Collectors.toList());
//     }

//     // @Override
//     // @Transactional
//     // public CommentResponseDTO save(CommentRequestDTO dto) {
//     //     Page page = pageRepository.findById(dto.pageId())
//     //             .orElseThrow(() -> new ResourceNotFoundException("La página no existe."));

//     //     UserEntity user = userRepository.findById(dto.userId())
//     //             .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe."));

//     //     Comment comment = commentMapper.toEntity(dto);
//     //     comment.setPage(page);
//     //     comment.setUser(user);

//     //     // AÑADIDO: Lógica para respuestas (hilos)
//     //     if (dto.parentId() != null) {
//     //         Comment parent = commentRepository.findById(dto.parentId())
//     //                 .orElseThrow(() -> new ResourceNotFoundException("No se puede responder: El comentario original no existe."));
//     //         comment.setParent(parent);
//     //     }

//     //     return commentMapper.toResponseDTO(commentRepository.save(comment));
//     // }

//     // // AÑADIDO: Método para marcar como resuelto (útil para FAQs/Soporte)
//     // @Override
//     // @Transactional
//     // public void toggleResolved(Long id) {
//     //     Comment comment = commentRepository.findById(id)
//     //             .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado."));
//     //     comment.setResolved(!comment.isResolved());
//     //     commentRepository.save(comment);
//     // }

//     @Override
//     @Transactional
//     public CommentResponseDTO save(CommentRequestDTO dto) {
//         // 1. Validar página y usuario (código existente)
//         Page page = pageRepository.findById(dto.pageId())
//                 .orElseThrow(() -> new ResourceNotFoundException("La página no existe."));
//         UserEntity user = userRepository.findById(dto.userId())
//                 .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe."));

//         // 2. Crear entidad y asignar relaciones
//         Comment comment = commentMapper.toEntity(dto);
//         comment.setPage(page);
//         comment.setUser(user);

//         // 3. Si es respuesta, establecer padre
//         if (dto.parentId() != null) {
//             Comment parent = commentRepository.findById(dto.parentId())
//                     .orElseThrow(() -> new ResourceNotFoundException("No se puede responder: El comentario original no existe."));
//             comment.setParent(parent);
//         }

//         // 4. Guardar el comentario
//         Comment savedComment = commentRepository.save(comment);

//         // 5. Si es una pregunta (sin padre), generar respuesta IA automáticamente
//         if (dto.parentId() == null) {
//             generateAiReplyAsync(savedComment.getId());
//         }

//         return commentMapper.toResponseDTO(savedComment);
//     }

//     @Async
//     @Transactional
//     public void generateAiReplyAsync(Long questionId) {
//         try {
//             // 1. Recuperar la pregunta
//             Comment question = commentRepository.findById(questionId)
//                     .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada con ID: " + questionId));

//             // 2. Contexto (título de la página)
//             String tituloLeccion = (question.getPage() != null) ? question.getPage().getTitle() : "Lección General";

//             // 3. Generar sugerencia con IA
//             String sugerencia = aiService.generateResponse(question.getContent(), tituloLeccion);

//             // 4. Buscar usuario especial para la IA (debe existir en BD)
//             //    Puedes buscarlo por email o ID fijo; aquí usamos email.
//             String iaEmail = "gamosadev@gmail.com";
//             UserEntity iaUser = userRepository.findByEmail(iaEmail)
//                     .orElseThrow(() -> new ResourceNotFoundException("Usuario IA no encontrado con email: " + iaEmail));

//             // 5. Crear comentario respuesta
//             Comment reply = Comment.builder()
//                     .content(sugerencia)
//                     .page(question.getPage())
//                     .user(iaUser)
//                     .parent(question)
//                     .resolved(false)
//                     .build();

//             // 6. Guardar la respuesta
//             commentRepository.save(reply);

//         } catch (Exception e) {
//             // Log del error (puedes usar un logger)
//             System.err.println("Error generando respuesta IA automática: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     // AÑADIDO: Listar dudas pendientes para el panel de administración
//     @Override
//     @Transactional(readOnly = true)
//     public List<CommentResponseDTO> findUnresolved() {
//         return commentRepository.findByResolvedFalseOrderByCreatedAtAsc().stream()
//                 .map(commentMapper::toResponseDTO)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     @Transactional(readOnly = true)
//     public List<CommentResponseDTO> findAll() {
//         // Para el listado global, solemos querer ver solo inicios de conversación
//         return commentRepository.findAllByOrderByCreatedAtDesc().stream()
//                 .map(commentMapper::toResponseDTO)
//                 .collect(Collectors.toList());
//     }

//     @Override
//     @Transactional
//     public void delete(Long id) {
//         if (!commentRepository.existsById(id)) {
//             throw new ResourceNotFoundException("El comentario no existe.");
//         }
//         commentRepository.deleteById(id);
//     }

//     @Override
//     public Comment findById(Long id) {
//         return commentRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado."));
//     }

//     @Override
//     @Transactional
//     public void toggleResolved(Long id) {
//         Comment comment = commentRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado."));
//         comment.setResolved(!comment.isResolved());
//         commentRepository.save(comment);
//     }
// }

package es.jlrn.presentation.universilabs.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
import es.jlrn.presentation.universilabs.services.IA.AIService;
import es.jlrn.presentation.universilabs.services.interfaces.ICommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements ICommentService {
//
    private final CommentRepository commentRepository;
    private final PageRepository pageRepository;
    private final UserRepository userRepository;
    private final ICommentMapper commentMapper;
    private final AIService aiService;

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> findByPageId(Long pageId) {
        if (!pageRepository.existsById(pageId)) {
            throw new ResourceNotFoundException("Página no encontrada con ID: " + pageId);
        }
        return commentRepository.findByPageIdAndParentIsNullOrderByCreatedAtDesc(pageId).stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDTO save(CommentRequestDTO dto) {
        // 1. Validar existencia de página y usuario
        Page page = pageRepository.findById(dto.pageId())
                .orElseThrow(() -> new ResourceNotFoundException("La página no existe."));
        UserEntity user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no existe."));

        // 2. VALIDACIÓN DE DUPLICADOS (Solo para preguntas nuevas, no respuestas)
        String cleanContent = dto.content().trim();
        if (dto.parentId() == null) {
            boolean exists = commentRepository.existsByContentIgnoreCaseAndPageId(cleanContent, dto.pageId());
            if (exists) {
                log.warn("Intento de pregunta duplicada detectado: '{}' en página {}", cleanContent, dto.pageId());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Esta duda ya ha sido planteada en esta lección. ¡Revisa los comentarios anteriores!");
            }
        }

        // 3. Mapear y configurar la entidad
        Comment comment = commentMapper.toEntity(dto);
        comment.setContent(cleanContent);
        comment.setPage(page);
        comment.setUser(user);

        // 4. Lógica de hilos (respuestas)
        if (dto.parentId() != null) {
            Comment parent = commentRepository.findById(dto.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se puede responder: El comentario original no existe."));
            comment.setParent(parent);
        }

        // 5. Guardar el comentario original
        Comment savedComment = commentRepository.save(comment);

        // 6. Si es pregunta, disparar IA de forma asíncrona
        if (dto.parentId() == null) {
            generateAiReplyAsync(savedComment.getId());
        }

        return commentMapper.toResponseDTO(savedComment);
    }

    @Async
    @Transactional
    public void generateAiReplyAsync(Long questionId) {
        try {
            Comment question = commentRepository.findById(questionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pregunta no encontrada con ID: " + questionId));

            String tituloLeccion = (question.getPage() != null) ? question.getPage().getTitle() : "Lección General";

            // Llamada al servicio IA
            String sugerencia = aiService.generateResponse(question.getContent(), tituloLeccion);

            // Usuario asignado a la IA
            String iaEmail = "gamosadev@gmail.com";
            UserEntity iaUser = userRepository.findByEmail(iaEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario IA no encontrado: " + iaEmail));

            // Crear la respuesta vinculada (reply)
            Comment reply = Comment.builder()
                    .content(sugerencia)
                    .page(question.getPage())
                    .user(iaUser)
                    .parent(question)
                    .resolved(true) // La IA resuelve la duda automáticamente
                    .build();

            commentRepository.save(reply);
            log.info("Respuesta IA generada con éxito para el ID: {}", questionId);

        } catch (Exception e) {
            log.error("Fallo al generar respuesta IA para ID {}: {}", questionId, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> findUnresolved() {
        return commentRepository.findByResolvedFalseOrderByCreatedAtAsc().stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> findAll() {
        return commentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new ResourceNotFoundException("El comentario no existe.");
        }
        commentRepository.deleteById(id);
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado."));
    }

    @Override
    @Transactional
    public void toggleResolved(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario no encontrado."));
        comment.setResolved(!comment.isResolved());
        commentRepository.save(comment);
    }
}