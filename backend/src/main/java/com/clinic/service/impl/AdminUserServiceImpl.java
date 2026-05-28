package com.clinic.service.impl;

import com.clinic.constant.Role;
import com.clinic.dto.request.AdminUpsertUserRequest;
import com.clinic.dto.response.AdminUserResponse;
import com.clinic.dto.response.PageResponse;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.UserRepository;
import com.clinic.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private AdminUserResponse toResponse(User u) {
        return AdminUserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .phoneNumber(u.getPhoneNumber())
                .role(u.getRole().name())
                .isActive(u.isActive())
                .createdAt(u.getCreatedAt())
                .build();
    }

    @Override
    public PageResponse<AdminUserResponse> getUsers(int page, int size, String q, String role, String status) {
        String keyword = (q == null || q.isBlank()) ? null : q.trim();
        String roleFilter = (role == null || role.isBlank()) ? null : role.trim().toUpperCase();
        Boolean activeFilter = "active".equalsIgnoreCase(status) ? Boolean.TRUE
                             : "inactive".equalsIgnoreCase(status) ? Boolean.FALSE
                            : null;

        Page<User> result = userRepository.findFiltered(keyword, roleFilter, activeFilter, PageRequest.of(page, size));
        return PageResponse.<AdminUserResponse>builder()
                .content(result.getContent().stream().map(this::toResponse).toList())
                .page(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public AdminUserResponse updateUser(Long id, AdminUpsertUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try { user.setRole(Role.valueOf(request.getRole())); } catch (IllegalArgumentException ignored) {}
        }
        if (request.getIsActive() != null) user.setActive(request.getIsActive());

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }
}
