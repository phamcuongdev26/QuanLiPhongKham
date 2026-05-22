package com.clinic.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    USER_NOT_FOUND(1001, "User không tồn tại", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1002, "Email đã tồn tại", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(1003, "Username đã tồn tại", HttpStatus.CONFLICT),
    WRONG_PASSWORD(1004, "Mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    UNAUTHENTICATED(1005, "Chưa đăng nhập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED(1007, "Tài khoản đã bị khóa", HttpStatus.FORBIDDEN),

    INVALID_EMAIL(2001, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(2002, "Mật khẩu phải ít nhất 6 ký tự", HttpStatus.BAD_REQUEST),

    SPECIALTY_NOT_FOUND(3001, "Chuyên khoa không tồn tại", HttpStatus.NOT_FOUND),
    SPECIALTY_ALREADY_EXISTS(3002, "Chuyên khoa đã tồn tại", HttpStatus.CONFLICT),
    DOCTOR_NOT_FOUND(3003, "Bác sĩ không tồn tại", HttpStatus.NOT_FOUND),

    INVALID_TIME_RANGE(4001, "Khoảng thời gian không hợp lệ", HttpStatus.BAD_REQUEST),
    APPOINTMENT_NOT_FOUND(4002, "Lịch hẹn không tồn tại", HttpStatus.NOT_FOUND),
    APPOINTMENT_TIME_UNAVAILABLE(4003, "Khung giờ đã được đặt", HttpStatus.CONFLICT),
    APPOINTMENT_INVALID_STATUS(4004, "Trạng thái lịch hẹn không hợp lệ", HttpStatus.BAD_REQUEST),

    PASSWORD_RESET_TOKEN_INVALID(5001, "Token đổi mật khẩu không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_RESET_TOKEN_EXPIRED(5002, "Token đổi mật khẩu đã hết hạn", HttpStatus.BAD_REQUEST),
    MAIL_NOT_CONFIGURED(5003, "Chưa cấu hình gửi email", HttpStatus.INTERNAL_SERVER_ERROR),

    INTERNAL_ERROR(9999, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR);

    int code;
    String message;
    HttpStatus httpStatus;
}

