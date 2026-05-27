# Hệ thống quản lý phòng khám/bệnh viện

Ứng dụng gồm backend Spring Boot, frontend HTML/CSS/JavaScript thuần và cơ sở dữ liệu MySQL. Dự án đã được đóng gói Docker để người khác pull code về có thể chạy không cần cài Java, Maven hay MySQL trên máy.

## Yêu cầu

- Docker Desktop
- Git

## Chạy nhanh bằng Docker

```powershell
git clone <link-repository>
cd QuanLiPhongKham
docker compose up -d --build
```

Mở trình duyệt:

```text
http://localhost:8080
```

Nếu máy đã dùng cổng `8080`, tạo file `.env` từ `.env.example` rồi đổi `APP_PORT`, ví dụ:

```env
APP_PORT=8081
MYSQL_PORT=3307
```

Sau đó chạy lại:

```powershell
docker compose up -d --build
```

Khi đổi `APP_PORT=8081`, mở:

```text
http://localhost:8081
```

## Tài khoản mẫu

Tất cả tài khoản mẫu dùng mật khẩu:

```text
Admin@123
```

| Vai trò | Username |
| --- | --- |
| Admin | `admin` |
| Bác sĩ | `bs_minh` |
| Bác sĩ | `bs_lan` |
| Bệnh nhân | `patient1` |

## Reset dữ liệu Docker

MySQL chỉ chạy file `schema.sql` khi volume database được tạo lần đầu. Nếu đã từng chạy bản cũ và muốn nạp lại dữ liệu mẫu:

```powershell
docker compose down -v
docker compose up -d --build
```

Lệnh `down -v` sẽ xóa dữ liệu database cũ trong Docker volume.

## Các service Docker

- `app`: Spring Boot + frontend, mặc định chạy ở `http://localhost:8080`
- `db`: MySQL 8, mặc định map ra máy host ở cổng `3307`

Ứng dụng kết nối database bằng network nội bộ Docker, nên không cần cài MySQL trên máy.

## Lệnh kiểm tra

```powershell
docker compose ps
docker compose logs -f app
docker compose logs -f db
```

## Cấu trúc chính

```text
QuanLiPhongKham/
├── backend/             Spring Boot API
├── frontend/            HTML, CSS, JavaScript
├── schema.sql           Schema và dữ liệu mẫu
├── Dockerfile           Build backend và đóng frontend vào image
├── docker-compose.yml   Chạy app + MySQL
├── .env.example         Cấu hình cổng và database mẫu
└── README.md
```
