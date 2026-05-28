package com.clinic.service.impl;

import com.clinic.constant.Role;
import com.clinic.dto.request.LoginRequest;
import com.clinic.dto.request.RegisterRequest;
import com.clinic.dto.response.AuthResponse;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.UserRepository;
import com.clinic.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-hours}")
    private long expirationHours;

    private String generateToken(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(expirationHours, ChronoUnit.HOURS))
                .claim("scope", user.getRole().name())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.usernameExists(request.getUsername()))
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        if (userRepository.emailExists(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.PATIENT)
                .isActive(true)
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(generateToken(user))
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Đăng ký thành công!")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findByLogin(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive())
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new AppException(ErrorCode.WRONG_PASSWORD);

        return AuthResponse.builder()
                .accessToken(generateToken(user))
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .message("Đăng nhập thành công!")
                .build();
    }
}
