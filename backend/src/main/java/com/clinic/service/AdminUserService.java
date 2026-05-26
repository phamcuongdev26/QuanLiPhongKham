package com.clinic.service;

import com.clinic.dto.request.AdminCreateUserRequest;
import com.clinic.dto.request.AdminUpdateUserRequest;
import com.clinic.dto.response.ApiResponse;
import com.clinic.dto.response.PageResponse;
import com.clinic.dto.response.UserResponse;

public interface AdminUserService {

    ApiResponse<PageResponse<UserResponse>> listUsers(int page, int size, String q, String status, String role);

    UserResponse createUser(AdminCreateUserRequest request);

    UserResponse updateUser(Long id, AdminUpdateUserRequest request);

    ApiResponse<Void> toggleActive(Long id);

    ApiResponse<Void> deleteUser(Long id);
}
