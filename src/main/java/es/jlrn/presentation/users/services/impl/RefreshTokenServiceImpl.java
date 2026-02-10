package es.jlrn.presentation.users.services.impl;

import es.jlrn.persistence.models.users.models.RefreshTokenEntity;
import es.jlrn.persistence.models.users.models.UserEntity;
import es.jlrn.persistence.models.users.repositories.RefreshTokenRepository;
import es.jlrn.presentation.users.services.interfaces.IRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements IRefreshTokenService {
//
   private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenEntity createRefreshToken(UserEntity user, long durationMinutes, String ip, String userAgent) {
        String token = UUID.randomUUID().toString() + "." + UUID.randomUUID();

        RefreshTokenEntity refresh = RefreshTokenEntity.builder()
            .token(token)
            .user(user)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMinutes(durationMinutes))
            .revoked(false)
            .expired(false)
            .ipAddress(ip)
            .userAgent(userAgent)
            .build();

        return refreshTokenRepository.save(refresh);
    }

    //
    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    //
    public boolean isExpired(RefreshTokenEntity token) {
        return token.getExpiresAt().isBefore(LocalDateTime.now());
    }

    //
    public void revokeToken(RefreshTokenEntity token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    //
    public void expireToken(RefreshTokenEntity token) {
        token.setExpired(true);
        refreshTokenRepository.save(token);
    }

    //
    public RefreshTokenEntity rotateToken(RefreshTokenEntity oldToken, long durationMinutes) {
        revokeToken(oldToken);
        return createRefreshToken(oldToken.getUser(), durationMinutes, oldToken.getIpAddress(), oldToken.getUserAgent());
    }
}