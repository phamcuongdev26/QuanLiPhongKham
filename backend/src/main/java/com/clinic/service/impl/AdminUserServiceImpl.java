package com.clinic.service.impl;

import com.clinic.constant.Role;
import com.clinic.dto.request.AdminCreateUserRequest;
import com.clinic.dto.request.AdminUpdateUserRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.PageResponse;
import com.clinic.dto.response.UserResponse;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.UserRepository;
import com.clinic.service.AdminUserService;
import com.clinic.service.AuditLogService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String currentAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public ApiResponse<PageResponse<UserResponse>> listUsers(int page, int size, String q, String status, String role) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // This section is for patients only; doctors and admins are managed elsewhere
            predicates.add(cb.equal(root.get("role"), Role.PATIENT));

            if (q != null && !q.isBlank()) {
                String like = "%" + q.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("fullName")), like)
                ));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("isActive"), "active".equals(status)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> result = userRepository.findAll(spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .content(result.getContent().stream().map(this::toResponse).toList())
                .page(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalElements(result.getTotalElements())
                .build();

        return ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .result(pageResponse)
                .build();
    }

    @Override
    public UserResponse createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role userRole;
        try {
            userRole = Role.valueOf(request.getRole());
        } catch (Exception e) {
            userRole = Role.PATIENT;
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(userRole)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        User saved = userRepository.save(user);
        auditLogService.log("CREATE", "USER", saved.getId(), saved.getUsername(),
                currentAdmin(), null,
                "Tạo user mới: " + saved.getUsername() + " (" + saved.getRole() + ")",
                null, null);
        return toResponse(saved);
    }

    @Override
    public UserResponse updateUser(Long id, AdminUpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String oldSnapshot = snapshotOf(user);

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!request.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                user.setRole(Role.valueOf(request.getRole()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (request.getIsActive() != null) {
            user.setActive(request.getIsActive());
        }

        User saved = userRepository.save(user);
        auditLogService.log("UPDATE", "USER", saved.getId(), saved.getUsername(),
                currentAdmin(), null,
                "Cập nhật user: " + saved.getUsername(),
                oldSnapshot, snapshotOf(saved));
        return toResponse(saved);
    }

    @Override
    public ApiResponse<Void> toggleActive(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        boolean wasActive = user.isActive();
        user.setActive(!wasActive);
        userRepository.save(user);
        auditLogService.log("UPDATE", "USER", user.getId(), user.getUsername(),
                currentAdmin(), null,
                (wasActive ? "Khóa" : "Mở khóa") + " tài khoản: " + user.getUsername(),
                null, null);
        return ApiResponse.<Void>builder()
                .code(200)
                .message(user.isActive() ? "Tài khoản đã được mở khóa" : "Tài khoản đã bị khóa")
                .build();
    }

    @Override
    public ApiResponse<Void> deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String snapshot = snapshotOf(user);
        userRepository.delete(user);
        auditLogService.log("DELETE", "USER", id, user.getUsername(),
                currentAdmin(), null,
                "Xóa user: " + user.getUsername(),
                snapshot, null);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Xóa user thành công")
                .build();
    }

    private String snapshotOf(User u) {
        return String.format("{\"username\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"isActive\":%b}",
                u.getUsername(), u.getEmail(), u.getRole(), u.isActive());
    }
}
