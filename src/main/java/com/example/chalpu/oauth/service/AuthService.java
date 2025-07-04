package com.example.chalpu.oauth.service;

import com.example.chalpu.common.exception.AuthException;
import com.example.chalpu.common.exception.ErrorMessage;
import com.example.chalpu.common.exception.UserException;

import com.example.chalpu.oauth.dto.AccessTokenDTO;
import com.example.chalpu.oauth.dto.RefreshTokenDTO;
import com.example.chalpu.oauth.dto.TokenDTO;
import com.example.chalpu.oauth.security.jwt.JwtTokenProvider;
import com.example.chalpu.oauth.security.jwt.UserDetailsImpl;
import com.example.chalpu.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AccessTokenDTO refreshToken(RefreshTokenDTO refreshToken) {
        // 토큰 검증 로직을 AuthService로 이동
        User user = validateAndGetUser(refreshToken.getRefreshToken());

        String newAccessToken = tokenProvider.generateAccessToken(user.getId(), user.getEmail(), user.getRole().name());

        return new AccessTokenDTO(newAccessToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteRefreshTokenByUserId(userId);
        log.info("사용자 {} 로그아웃 완료", userId);
    }



    // 공통 검증 로직
    private User validateAndGetUser(String refreshToken) {
        if (refreshToken == null) {
            throw new AuthException(ErrorMessage.AUTH_REFRESH_TOKEN_NOT_FOUND);
        }

        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            throw new AuthException(ErrorMessage.AUTH_INVALID_REFRESH_TOKEN);
        }

        return refreshTokenService.getUserByRefreshToken(refreshToken)
                .orElseThrow(() -> new UserException(ErrorMessage.USER_NOT_FOUND));
    }
}

