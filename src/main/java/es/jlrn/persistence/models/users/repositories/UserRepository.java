package es.jlrn.persistence.models.users.repositories;

import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.presentation.users.dtos.Usuarios.UsersProfileDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // 🔹 Búsquedas simples
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    // 🔹 Existencias básicas
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);

    // 🔹 Usuarios activos
    boolean existsByEmailAndActivoTrue(String email);
    boolean existsByUsernameAndActivoTrue(String username);

    Optional<UserEntity> findByUsernameIgnoreCaseAndActivoTrue(String username);


    Optional<UserEntity> findByEmailAndActivoTrue(String email);
    Optional<UserEntity> findByUsernameAndActivoTrue(String username);
    Optional<UserEntity> findByEmailIgnoreCaseAndActivoTrue(String email);

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.username) = LOWER(:username) AND u.activo = true")
    //Optional<UserEntity> findByUsernameIgnoreCaseAndActivoTrue(@Param("username") String username);

    // 🔹 Búsquedas con paginación
    Page<UserEntity> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<UserEntity> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String username, String email, Pageable pageable
    );

    Optional<UserEntity> findByPasswordResetToken(String token);

    // 🔹 Borrar usuarios no verificados
    @Modifying
    @Transactional
    @Query("DELETE FROM UserEntity u WHERE u.emailVerified = false AND u.verificationCodeExpiry <= :now")
    int deleteExpiredUnverifiedUsers(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserEntity u WHERE u.emailVerified = false AND u.email = :email AND u.verificationCodeExpiry <= :now")
    int deleteUnverifiedUsersByEmail(@Param("email") String email, @Param("now") LocalDateTime now);
    
    // 🔹 Consultas explícitas para usuarios no verificados
    @Query("SELECT u FROM UserEntity u WHERE u.emailVerified = false AND u.verificationCodeExpiry <= :now")
    List<UserEntity> findExpiredUnverifiedUsers(@Param("now") LocalDateTime now);

    @Query("SELECT u FROM UserEntity u WHERE u.emailVerified = false AND u.email = :email AND u.verificationCodeExpiry <= :now")
    List<UserEntity> findUnverifiedUsersByEmailExpired(@Param("email") String email, @Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserEntity u WHERE u.id = :userId AND u.emailVerified = false")
    int deleteIfNotVerified(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE UserEntity u
        SET u.passwordResetCode = null,
            u.passwordResetCodeExpiry = null,
            u.passwordResetToken = null,
            u.passwordResetTokenExpiry = null,
            u.passwordResetConfirmed = false
        WHERE u.email = :email
    """)
    int clearForgotPasswordByEmail(@Param("email") String email);

    // En UserRepository.java
    @Query("SELECT u FROM UserEntity u " +
        "LEFT JOIN FETCH u.roles r " +
        "LEFT JOIN FETCH r.permissions " +
        "WHERE u.email = :input OR u.username = :input")
    Optional<UserEntity> findByEmailOrUsernameWithRoles(@Param("input") String input);

    @Query("SELECT u FROM UserEntity u " +
       "LEFT JOIN FETCH u.profile p " +
       "LEFT JOIN FETCH u.roles r " +
       "WHERE u.username = :username OR u.email = :username")
    Optional<UserEntity> findUserProfileByUsername(@Param("username") String username);
}
