# Clinic MVP - Hệ thống quản lý phòng khám

## Công nghệ
- **Backend**: Spring Boot 3.5, Spring Security (JWT/RSA), Spring Data JPA
- **Database**: MySQL 8+
- **Frontend**: HTML/CSS/JS thuần (không framework)

## Cấu trúc dự án
```
shoppe_mvp/
├── backend/          Spring Boot (package: com.clinic)
├── frontend/         HTML pages
├── schema.sql        DB schema + seed data
└── README.md
```

## Database

### Thông tin kết nối
| Thông số | Giá trị |
|----------|---------|
| Host | `localhost:3306` |
| Database | `clinic_db` |
| Username | `root` |
| Password | `12345678` |
| Charset | `utf8mb4_unicode_ci` |

### Connection URL
```
jdbc:mysql://localhost:3306/clinic_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
```

### Các bảng chính
| Bảng | Mô tả |
|------|-------|
| `users` | Người dùng (ADMIN / DOCTOR / PATIENT) |
| `specialties` | Chuyên khoa |
| `doctor_profiles` | Hồ sơ bác sĩ (liên kết `users`) |
| `doctor_work_shifts` | Ca làm việc của bác sĩ |
| `doctor_days_off` | Ngày nghỉ của bác sĩ |
| `appointments` | Lịch hẹn khám |
| `medical_records` | Hồ sơ bệnh án (1-1 với appointment) |
| `prescriptions` | Đơn thuốc (1-1 với appointment) |
| `prescription_items` | Chi tiết từng thuốc trong đơn |
| `password_reset_tokens` | Token đặt lại mật khẩu |
| `notifications` | Thông báo |

### Khởi tạo DB
```bash
# Chạy schema.sql — tạo DB, bảng và seed data trong một lần
mysql -u root -p < schema.sql
```

---

## Cài đặt

### 1. Tạo database
```bash
mysql -u root -p < schema.sql
```

### 2. Tạo RSA Key pair
```bash
openssl genpkey -algorithm RSA -out private_key.pem -pkcs8
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

### 3. Set environment variables
```bash
export DB_URL=jdbc:mysql://localhost:3306/clinic_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
export DB_USERNAME=root
export DB_PASSWORD=12345678
export RSA_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----"
export RSA_PUBLIC_KEY="-----BEGIN PUBLIC KEY-----\n...\n-----END PUBLIC KEY-----"
```

### 4. Chạy backend
```bash
cd backend
mvn spring-boot:run
```

### 5. Truy cập
- **Login**: http://localhost:8080/login.html

## Tài khoản mặc định

| Role    | Username   | Password     |
|---------|------------|--------------|
| Admin   | `admin`    | `Admin@123`  |
| Bác sĩ  | `bs_minh`  | `Doctor@123` |
| Bác sĩ  | `bs_lan`   | `Doctor@123` |
| Bệnh nhân | `patient1` | `Patient@123` |

## Luồng sử dụng

### Bệnh nhân
`Đăng nhập` → `Chọn chuyên khoa` → `Chọn bác sĩ` → `Chọn ngày/giờ` → `Đặt lịch` → `Xem lịch của tôi`

### Bác sĩ
`Đăng nhập` → `Xem lịch hẹn hôm nay` → `Xác nhận/Từ chối` → `Ghi hồ sơ bệnh án + Kê đơn thuốc`

### Admin
`Đăng nhập` → `Dashboard thống kê` → `Quản lý bác sĩ / Chuyên khoa / Users`

## API Endpoints

### Auth (Public)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/auth/register` | Đăng ký |
| POST | `/api/auth/login` | Đăng nhập → JWT |
| POST | `/api/auth/forgot-password` | Quên mật khẩu |
| POST | `/api/auth/reset-password` | Đặt lại mật khẩu |

### Public
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/specialties` | Danh sách chuyên khoa |
| GET | `/api/doctors` | Danh sách bác sĩ |
| GET | `/api/doctors?specialtyId=` | Bác sĩ theo chuyên khoa |
| GET | `/api/doctors/{id}` | Chi tiết bác sĩ |
| GET | `/api/availability/doctor-slots?doctorId=&date=` | Khung giờ trống |

### Patient (cần PATIENT token)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/patient/appointments` | Đặt lịch |
| GET | `/api/patient/appointments` | Lịch của tôi |
| DELETE | `/api/patient/appointments/{id}` | Hủy lịch |

### Doctor (cần DOCTOR token)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/doctor/appointments/today?date=` | Lịch hẹn theo ngày |
| PATCH | `/api/doctor/appointments/{id}/status` | Cập nhật trạng thái |
| PUT | `/api/doctor/medical/appointments/{id}/record` | Lưu hồ sơ bệnh án |
| PUT | `/api/doctor/medical/appointments/{id}/prescription` | Lưu đơn thuốc |
| POST/GET | `/api/doctor/schedule/shifts` | Ca làm việc |
| POST/GET | `/api/doctor/schedule/days-off` | Ngày nghỉ |

### Admin (cần ADMIN token)
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/admin/dashboard/overview` | Thống kê tổng quan |
| GET/POST/PUT/DELETE | `/api/admin/users` | Quản lý users |
| GET/POST/DELETE | `/api/admin/doctors` | Quản lý bác sĩ |
| GET/POST/PUT/DELETE | `/api/admin/specialties` | Quản lý chuyên khoa |
| GET/PATCH | `/api/admin/appointments` | Quản lý lịch hẹn |
