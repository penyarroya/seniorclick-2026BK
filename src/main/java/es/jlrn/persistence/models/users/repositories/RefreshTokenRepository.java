package es.jlrn.persistence.models.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.jlrn.persistence.models.users.models.RefreshTokenEntity;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    // Corregido: Spring Data navegará automáticamente de user -> id
    List<RefreshTokenEntity> findAllByUserIdAndRevokedAtIsNull(Long userId);

    boolean existsByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt <= :now OR r.revokedAt IS NOT NULL")
    int deleteByExpiresAtBeforeOrRevokedAtIsNotNull(@Param("now") LocalDateTime now);
    
    // Corregido: r.user.id (coincide con el @Id de UserEntity)
    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity r SET r.revokedAt = :now WHERE r.user.id = :userId AND r.revokedAt IS NULL")
    void revokeAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}

// @Repository
// public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
// //
//     Optional<RefreshTokenEntity> findByToken(String token);
//     List<RefreshTokenEntity> findAllByUserIdAndRevokedFalse(Long userId);
//     boolean existsByToken(String token);

//     @Transactional
//     @Modifying
//     @Query("DELETE FROM RefreshTokenEntity r WHERE r.expiresAt <= :now OR r.revoked = true")
//     int deleteByExpiresAtBeforeOrRevokedTrue(@Param("now") LocalDateTime now);

// }