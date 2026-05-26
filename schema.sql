-- ============================================================
-- CLINIC MVP - FULL DATABASE SCHEMA
-- Database: clinic_db (MySQL 8+)
-- ============================================================

CREATE DATABASE IF NOT EXISTS clinic_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE clinic_db;

-- ── USERS ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL UNIQUE,
  email       VARCHAR(120) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,
  full_name   VARCHAR(120),
  phone_number VARCHAR(20),
  role        VARCHAR(20)  NOT NULL COMMENT 'ADMIN | DOCTOR | PATIENT',
  is_active   TINYINT(1)   NOT NULL DEFAULT 1,
  created_at  DATETIME,
  updated_at  DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── SPECIALTIES ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS specialties (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(120) NOT NULL UNIQUE,
  description VARCHAR(500),
  is_active   TINYINT(1)   NOT NULL DEFAULT 1,
  created_at  DATETIME,
  updated_at  DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── DOCTOR PROFILES ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS doctor_profiles (
  user_id          BIGINT PRIMARY KEY,
  specialty_id     BIGINT,
  title            VARCHAR(200),
  bio              VARCHAR(1000),
  consultation_fee BIGINT COMMENT 'VND',
  created_at       DATETIME,
  updated_at       DATETIME,
  CONSTRAINT fk_dp_user      FOREIGN KEY (user_id)      REFERENCES users(id)      ON DELETE CASCADE,
  CONSTRAINT fk_dp_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── DOCTOR WORK SHIFTS ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS doctor_work_shifts (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  doctor_id   BIGINT      NOT NULL,
  day_of_week VARCHAR(10) NOT NULL COMMENT 'MONDAY..SUNDAY',
  start_time  TIME        NOT NULL,
  end_time    TIME        NOT NULL,
  is_active   TINYINT(1)  NOT NULL DEFAULT 1,
  CONSTRAINT fk_dws_doctor FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── DOCTOR DAYS OFF ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS doctor_days_off (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  doctor_id BIGINT NOT NULL,
  day_off   DATE   NOT NULL,
  reason    VARCHAR(255),
  UNIQUE KEY uq_doctor_day (doctor_id, day_off),
  CONSTRAINT fk_ddo_doctor FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── APPOINTMENTS ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS appointments (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  patient_id          BIGINT      NOT NULL,
  doctor_id           BIGINT      NOT NULL,
  specialty_id        BIGINT,
  start_time          DATETIME    NOT NULL,
  end_time            DATETIME    NOT NULL,
  symptom_description VARCHAR(2000),
  status              VARCHAR(20) NOT NULL COMMENT 'PENDING | CONFIRMED | REJECTED | COMPLETED | CANCELED',
  doctor_note         VARCHAR(2000),
  created_at          DATETIME,
  updated_at          DATETIME,
  INDEX idx_appointments_doctor_time  (doctor_id, start_time),
  INDEX idx_appointments_patient_time (patient_id, start_time),
  CONSTRAINT fk_appt_patient   FOREIGN KEY (patient_id)   REFERENCES users(id)      ON DELETE RESTRICT,
  CONSTRAINT fk_appt_doctor    FOREIGN KEY (doctor_id)    REFERENCES users(id)      ON DELETE RESTRICT,
  CONSTRAINT fk_appt_specialty FOREIGN KEY (specialty_id) REFERENCES specialties(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── MEDICAL RECORDS ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS medical_records (
  appointment_id BIGINT PRIMARY KEY,
  diagnosis      VARCHAR(2000),
  clinical_note  VARCHAR(4000),
  created_at     DATETIME,
  updated_at     DATETIME,
  CONSTRAINT fk_mr_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── PRESCRIPTIONS ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS prescriptions (
  appointment_id BIGINT PRIMARY KEY,
  note           VARCHAR(2000),
  created_at     DATETIME,
  CONSTRAINT fk_prx_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── PRESCRIPTION ITEMS ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS prescription_items (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  prescription_id BIGINT       NOT NULL,
  drug_name       VARCHAR(200) NOT NULL,
  dosage          VARCHAR(200),
  frequency       VARCHAR(200),
  duration        VARCHAR(200),
  instruction     VARCHAR(500),
  CONSTRAINT fk_pi_prescription FOREIGN KEY (prescription_id) REFERENCES prescriptions(appointment_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── AUDIT LOGS ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS audit_logs (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  action         VARCHAR(20)  NOT NULL COMMENT 'CREATE | UPDATE | DELETE',
  entity_type    VARCHAR(50)  NOT NULL COMMENT 'USER | SPECIALTY | APPOINTMENT | DOCTOR',
  entity_id      BIGINT,
  entity_name    VARCHAR(255),
  admin_username VARCHAR(50),
  admin_full_name VARCHAR(120),
  ip_address     VARCHAR(45),
  detail         VARCHAR(500),
  old_value      TEXT,
  new_value      TEXT,
  created_at     DATETIME,
  INDEX idx_audit_entity_type (entity_type),
  INDEX idx_audit_action      (action),
  INDEX idx_audit_created_at  (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── PASSWORD RESET TOKENS ────────────────────────────────────
CREATE TABLE IF NOT EXISTS password_reset_tokens (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT       NOT NULL,
  token      VARCHAR(128) NOT NULL UNIQUE,
  expires_at DATETIME     NOT NULL,
  used_at    DATETIME,
  INDEX idx_password_reset_token (token),
  CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ── NOTIFICATIONS ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notifications (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    BIGINT       NOT NULL,
  type       VARCHAR(50)  NOT NULL,
  title      VARCHAR(255),
  body       VARCHAR(2000),
  is_read    TINYINT(1)   NOT NULL DEFAULT 0,
  created_at DATETIME,
  CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Tất cả tài khoản test dùng password: Admin@123
-- Hash: $2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a
INSERT IGNORE INTO users (username, full_name, email, password, role, is_active, created_at, updated_at) VALUES
('admin', 'Super Admin', 'admin@clinic.com',
 '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a',
 'ADMIN', 1, NOW(), NOW());

-- SPECIALTIES
INSERT IGNORE INTO specialties (name, description, is_active, created_at, updated_at) VALUES
('Tim mạch',        'Khám và điều trị các bệnh lý tim mạch',                        1, NOW(), NOW()),
('Thần kinh',       'Khám và điều trị các bệnh lý thần kinh',                        1, NOW(), NOW()),
('Nhi khoa',        'Khám và điều trị bệnh lý trẻ em',                               1, NOW(), NOW()),
('Da liễu',         'Khám và điều trị các bệnh về da',                               1, NOW(), NOW()),
('Mắt',             'Khám và điều trị các bệnh lý về mắt',                           1, NOW(), NOW()),
('Tai mũi họng',    'Khám và điều trị bệnh lý tai, mũi, họng',                       1, NOW(), NOW()),
('Cơ xương khớp',   'Khám và điều trị các bệnh về xương khớp',                       1, NOW(), NOW()),
('Nội tiết',        'Tiểu đường, tuyến giáp và các rối loạn nội tiết',               1, NOW(), NOW()),
('Tiêu hóa',        'Khám và điều trị bệnh lý đường tiêu hóa',                       1, NOW(), NOW()),
('Sản phụ khoa',    'Khám và chăm sóc sức khỏe phụ nữ và thai sản',                 1, NOW(), NOW());

-- DOCTORS (password: Admin@123)
INSERT IGNORE INTO users (username, full_name, email, password, role, is_active, created_at, updated_at) VALUES
('bs_minh',   'BS. Nguyễn Văn Minh',  'bs.minh@clinic.com',   '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'DOCTOR', 1, NOW(), NOW()),
('bs_lan',    'BS. Trần Thị Lan',     'bs.lan@clinic.com',    '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'DOCTOR', 1, NOW(), NOW()),
('bs_hung',   'BS. Lê Hoàng Hùng',   'bs.hung@clinic.com',   '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'DOCTOR', 1, NOW(), NOW()),
('bs_thu',    'BS. Phạm Thị Thu',    'bs.thu@clinic.com',    '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'DOCTOR', 1, NOW(), NOW()),
('bs_duc',    'BS. Hoàng Văn Đức',   'bs.duc@clinic.com',    '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'DOCTOR', 1, NOW(), NOW());

-- DOCTOR PROFILES (linked to specialty IDs 1..5)
INSERT IGNORE INTO doctor_profiles (user_id, specialty_id, title, bio, consultation_fee, created_at, updated_at)
SELECT u.id,
       s.id,
       dp.title, dp.bio, dp.fee,
       NOW(), NOW()
FROM (
  SELECT 'bs_minh' AS uname, 'Tim mạch'   AS sname, 'Tiến sĩ, Bác sĩ CKI'  AS title, 'Hơn 10 năm kinh nghiệm điều trị bệnh lý tim mạch tại các bệnh viện lớn.' AS bio, 300000 AS fee
  UNION ALL
  SELECT 'bs_lan',  'Thần kinh', 'Thạc sĩ, Bác sĩ CKII', 'Chuyên gia đầu ngành về các bệnh đột quỵ và rối loạn thần kinh.', 350000
  UNION ALL
  SELECT 'bs_hung', 'Nhi khoa',  'Bác sĩ CKI',            'Bác sĩ chuyên khoa nhi, có nhiều kinh nghiệm điều trị bệnh trẻ em.', 200000
  UNION ALL
  SELECT 'bs_thu',  'Da liễu',   'Thạc sĩ Y khoa',        'Chuyên điều trị các bệnh da mãn tính, dị ứng và chăm sóc da.', 250000
  UNION ALL
  SELECT 'bs_duc',  'Mắt',       'Bác sĩ CKII',           'Phẫu thuật mắt, điều trị đục thủy tinh thể và các bệnh về mắt.', 280000
) dp
JOIN users     u ON u.username = dp.uname
JOIN specialties s ON s.name   = dp.sname;

-- DOCTOR WORK SHIFTS
INSERT IGNORE INTO doctor_work_shifts (doctor_id, day_of_week, start_time, end_time, is_active)
SELECT u.id, d.day, d.s, d.e, 1
FROM (
  SELECT 'bs_minh' AS uname, 'MONDAY'    AS day, '08:00' AS s, '12:00' AS e UNION ALL
  SELECT 'bs_minh',           'WEDNESDAY',        '08:00',     '12:00' UNION ALL
  SELECT 'bs_minh',           'FRIDAY',           '13:00',     '17:00' UNION ALL
  SELECT 'bs_lan',            'TUESDAY',          '08:00',     '11:30' UNION ALL
  SELECT 'bs_lan',            'THURSDAY',         '13:00',     '17:00' UNION ALL
  SELECT 'bs_hung',           'MONDAY',           '13:00',     '17:00' UNION ALL
  SELECT 'bs_hung',           'WEDNESDAY',        '13:00',     '17:00' UNION ALL
  SELECT 'bs_thu',            'TUESDAY',          '08:00',     '12:00' UNION ALL
  SELECT 'bs_thu',            'FRIDAY',           '08:00',     '12:00' UNION ALL
  SELECT 'bs_duc',            'THURSDAY',         '08:00',     '11:30' UNION ALL
  SELECT 'bs_duc',            'SATURDAY',         '08:00',     '11:30'
) d
JOIN users u ON u.username = d.uname;

-- PATIENTS (password: Admin@123)
INSERT IGNORE INTO users (username, full_name, email, password, role, is_active, created_at, updated_at) VALUES
('patient1', 'Nguyễn Thị Hoa',   'hoa.nguyen@gmail.com',    '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'PATIENT', 1, NOW(), NOW()),
('patient2', 'Trần Minh Tuấn',   'tuan.tran@gmail.com',     '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'PATIENT', 1, NOW(), NOW()),
('patient3', 'Lê Thị Mai',       'mai.le@gmail.com',        '$2a$10$slYQmyNdgTY18LdlMqhIme5bIk6RlVAFxMpKNMC.5UqSqHKv6bC.a', 'PATIENT', 1, NOW(), NOW());
