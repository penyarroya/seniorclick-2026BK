package es.jlrn.presentation.users.services.interfaces;

import es.jlrn.persistence.models.users.models.RefreshTokenEntity;
import es.jlrn.persistence.models.users.models.UserEntity;

public interface IRefreshTokenService {
//
    RefreshTokenEntity createRefreshToken(UserEntity user, long durationMinutes, String ip, String userAgent);
    java.util.Optional<RefreshTokenEntity> findByToken(String token);
    boolean isExpired(RefreshTokenEntity token);
    void revokeToken(RefreshTokenEntity token); 
    void expireToken(RefreshTokenEntity token);
    RefreshTokenEntity rotateToken(RefreshTokenEntity oldToken, long durationMinutes);
}
