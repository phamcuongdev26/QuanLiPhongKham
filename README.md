# Clinic Management System

Hệ thống quản lý phòng khám/bệnh viện nhỏ, gồm backend Spring Boot, MySQL và frontend HTML/CSS/JavaScript thuần.

## Công nghệ

- Backend: Spring Boot 3.5, Spring Security, JWT/RSA, Spring Data JPA
- Database: MySQL 8
- Frontend: HTML, CSS, JavaScript
- Runtime khuyến nghị: Docker Compose

## Cấu trúc

```text
QuanLiPhongKham/
├── backend/            Spring Boot API
├── frontend/           HTML pages
├── schema.sql          Database schema và dữ liệu mẫu
├── docker-compose.yml  MySQL + application
└── Dockerfile
```

## Chạy bằng Docker

```powershell
cd D:\quanlyBV\QuanLiPhongKham
docker compose up -d --build
```

Mở ứng dụng:

```text
http://localhost:8080/login.html
```

## Tài khoản mẫu

| Vai trò | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `Admin@123` |
| Bác sĩ | `bs_minh` | `Admin@123` |
| Bác sĩ | `bs_lan` | `Admin@123` |
| Bệnh nhân | `patient1` | `Admin@123` |

## API chính

### Public/Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `GET /api/specialties`
- `GET /api/doctors`
- `GET /api/doctors/{id}`
- `GET /api/availability/doctor-slots?doctorId=&date=&slotMinutes=`

### Admin

- `GET /api/admin/stats`
- `GET /api/admin/users`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `PATCH /api/admin/users/{id}/toggle-active`
- `GET /api/admin/doctors`
- `POST /api/admin/doctors`
- `PUT /api/admin/doctors/{id}`
- `PATCH /api/admin/doctors/{id}/toggle-active`
- `GET /api/admin/specialties`
- `POST /api/admin/specialties`
- `DELETE /api/admin/specialties/{id}`
- `GET /api/admin/appointments`
- `PATCH /api/admin/appointments/{id}/status`
- `GET /api/admin/audit-logs`

### Doctor

- `GET /api/doctor/appointments/today?date=YYYY-MM-DD`
- `PATCH /api/doctor/appointments/{id}/status`
- `PUT /api/doctor/medical/appointments/{id}/record`
- `PUT /api/doctor/medical/appointments/{id}/prescription`
- `GET /api/doctor/schedule/shifts`
- `POST /api/doctor/schedule/shifts`
- `GET /api/doctor/schedule/days-off`
- `POST /api/doctor/schedule/days-off`

### Patient

- `GET /api/appointments`
- `POST /api/appointments`
- `DELETE /api/appointments/{id}`
- `GET /api/patient/notifications`
- `GET /api/patient/notifications/unread-count`
- `PATCH /api/patient/notifications/{id}/read`
- `PATCH /api/patient/notifications/read-all`
