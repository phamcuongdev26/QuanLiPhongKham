package com.clinic.service;

import com.clinic.dto.request.AdminUpsertUserRequest;
import com.clinic.dto.response.AdminUserResponse;
import com.clinic.dto.response.PageResponse;

public interface AdminUserService {
    PageResponse<AdminUserResponse> getUsers(int page, int size, String q, String role, String status);
    AdminUserResponse updateUser(Long id, AdminUpsertUserRequest request);
    void deleteUser(Long id);
    void toggleActive(Long id);
}
