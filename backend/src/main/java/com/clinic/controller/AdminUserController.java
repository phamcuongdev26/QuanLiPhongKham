package com.clinic.controller;

import com.clinic.dto.request.AdminUpsertUserRequest;
import com.clinic.dto.response.AdminUserResponse;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.PageResponse;
import com.clinic.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        PageResponse<AdminUserResponse> data = adminUserService.getUsers(page, size, q, role, status);
        return ResponseEntity.ok(ApiResponse.<PageResponse<AdminUserResponse>>builder()
                .code(200).result(data).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody AdminUpsertUserRequest request) {
        AdminUserResponse updated = adminUserService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.<AdminUserResponse>builder()
                .code(200).result(updated).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200).message("Đã vô hiệu hóa tài khoản").build());
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable Long id) {
        adminUserService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(200).message("Cập nhật trạng thái thành công").build());
    }
}
