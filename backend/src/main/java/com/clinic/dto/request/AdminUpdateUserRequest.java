package com.clinic.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateUserRequest {

    private String fullName;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String password;
    private String phoneNumber;
    private String address;
    private String role;
    private Boolean isActive;
}
