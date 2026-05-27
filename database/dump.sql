-- MySQL dump 10.13  Distrib 8.0.46, for Linux (aarch64)
--
-- Host: localhost    Database: clinic_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `clinic_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `clinic_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `clinic_db`;

--
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `patient_id` bigint NOT NULL,
  `doctor_id` bigint NOT NULL,
  `specialty_id` bigint DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `symptom_description` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'PENDING | CONFIRMED | REJECTED | COMPLETED | CANCELED',
  `doctor_note` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_appointments_doctor_time` (`doctor_id`,`start_time`),
  KEY `idx_appointments_patient_time` (`patient_id`,`start_time`),
  KEY `fk_appt_specialty` (`specialty_id`),
  CONSTRAINT `fk_appt_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_appt_patient` FOREIGN KEY (`patient_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_appt_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `specialties` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
INSERT INTO `appointments` VALUES (1,17,16,29,'2026-05-29 08:30:00','2026-05-29 09:00:00','lk','CANCELED',NULL,'2026-05-27 08:06:36','2026-05-27 08:41:49'),(2,17,5,31,'2026-06-26 08:30:00','2026-06-26 09:00:00',',k','COMPLETED',NULL,'2026-05-27 08:06:58','2026-05-27 08:12:13'),(3,7,5,31,'2026-05-29 08:30:00','2026-05-29 09:00:00',',','COMPLETED',NULL,'2026-05-27 08:19:04','2026-05-27 08:19:36'),(4,7,5,31,'2026-05-29 08:00:00','2026-05-29 08:30:00','aa','CONFIRMED',NULL,'2026-05-27 08:24:21','2026-05-27 08:25:05'),(5,7,5,31,'2026-05-29 10:00:00','2026-05-29 10:30:00','aa','CONFIRMED',NULL,'2026-05-27 08:24:32','2026-05-27 08:27:20'),(6,17,5,31,'2026-05-29 08:30:00','2026-05-29 09:00:00','s','CONFIRMED',NULL,'2026-05-27 08:42:03','2026-05-27 08:42:29');
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entity_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entity_id` bigint DEFAULT NULL,
  `entity_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `admin_username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `admin_full_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `detail` text COLLATE utf8mb4_unicode_ci,
  `old_value` text COLLATE utf8mb4_unicode_ci,
  `new_value` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_logs_created_at` (`created_at`),
  KEY `idx_audit_logs_entity_action` (`entity_type`,`action`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` (`id`,`action`,`entity_type`,`entity_id`,`entity_name`,`admin_username`,`admin_full_name`,`detail`,`created_at`) VALUES
(1,"UPDATE","DOCTOR",2,"BS. Nguyễn Văn Minh","admin","Super Admin","Cập nhật bác sĩ BS. Nguyễn Văn Minh","2026-05-26 20:04:34"),
(2,"UPDATE","DOCTOR",3,"BS. Trần Thị Lan","admin","Super Admin","Cập nhật bác sĩ: BS. Trần Thị Lan","2026-05-27 04:09:42"),
(3,"DELETE","DOCTOR",3,"BS. Trần Thị Lan","admin","Super Admin","Xóa bác sĩ: BS. Trần Thị Lan","2026-05-27 04:13:01"),
(4,"UPDATE","DOCTOR",3,"BS. Trần Thị Lan","admin","Super Admin","Cập nhật bác sĩ: BS. Trần Thị Lan","2026-05-27 04:13:09"),
(5,"UPDATE","DOCTOR",3,"BS. Trần Thị La","admin","Super Admin","Cập nhật bác sĩ: BS. Trần Thị La","2026-05-27 04:13:18"),
(6,"DELETE","DOCTOR",3,"BS. Trần Thị La","admin","Super Admin","Xóa bác sĩ: BS. Trần Thị La","2026-05-27 04:16:56"),
(7,"UPDATE","DOCTOR",3,"BS. Trần Thị Lan","admin","Super Admin","Cập nhật bác sĩ: BS. Trần Thị Lan","2026-05-27 04:19:09");
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_days_off`
--

DROP TABLE IF EXISTS `doctor_days_off`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_days_off` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `doctor_id` bigint NOT NULL,
  `day_off` date NOT NULL,
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_doctor_day` (`doctor_id`,`day_off`),
  UNIQUE KEY `UKlkeimm2rse32wjj1bdd2gpsa2` (`doctor_id`,`day_off`),
  CONSTRAINT `fk_ddo_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_days_off`
--

LOCK TABLES `doctor_days_off` WRITE;
/*!40000 ALTER TABLE `doctor_days_off` DISABLE KEYS */;
/*!40000 ALTER TABLE `doctor_days_off` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_profiles`
--

DROP TABLE IF EXISTS `doctor_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_profiles` (
  `user_id` bigint NOT NULL,
  `specialty_id` bigint DEFAULT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bio` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `consultation_fee` bigint DEFAULT NULL COMMENT 'VND',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `fk_dp_specialty` (`specialty_id`),
  CONSTRAINT `fk_dp_specialty` FOREIGN KEY (`specialty_id`) REFERENCES `specialties` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_dp_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_profiles`
--

LOCK TABLES `doctor_profiles` WRITE;
/*!40000 ALTER TABLE `doctor_profiles` DISABLE KEYS */;
INSERT INTO `doctor_profiles` VALUES (3,30,'Thạc sĩ, Bác sĩ CKII','Chuyên gia đầu ngành về các bệnh đột quỵ và rối loạn thần kinh.',350000,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(4,3,'Bác sĩ CKI','Bác sĩ chuyên khoa nhi, nhiều kinh nghiệm điều trị bệnh trẻ em.',200000,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(5,31,'Thạc sĩ Y khoa','Chuyên điều trị các bệnh da mãn tính, dị ứng và chăm sóc da.',250000,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(6,32,'Bác sĩ CKII','Phẫu thuật mắt, điều trị đục thủy tinh thể và các bệnh về mắt.',280000,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(16,29,'Tiến sĩ, Bác sĩ CKI','Hơn 10 năm kinh nghiệm điều trị bệnh lý tim mạch tại các bệnh viện lớn.',300000,'2026-05-27 03:56:13','2026-05-27 03:56:13');
/*!40000 ALTER TABLE `doctor_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_work_shifts`
--

DROP TABLE IF EXISTS `doctor_work_shifts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_work_shifts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `doctor_id` bigint NOT NULL,
  `day_of_week` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MONDAY..SUNDAY',
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_dws_doctor` (`doctor_id`),
  CONSTRAINT `fk_dws_doctor` FOREIGN KEY (`doctor_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_work_shifts`
--

LOCK TABLES `doctor_work_shifts` WRITE;
/*!40000 ALTER TABLE `doctor_work_shifts` DISABLE KEYS */;
INSERT INTO `doctor_work_shifts` VALUES (4,3,'TUESDAY','08:00:00','11:30:00',1),(5,3,'THURSDAY','13:00:00','17:00:00',1),(6,4,'MONDAY','13:00:00','17:00:00',1),(7,4,'WEDNESDAY','13:00:00','17:00:00',1),(8,5,'TUESDAY','08:00:00','12:00:00',1),(9,5,'FRIDAY','08:00:00','12:00:00',1),(10,6,'THURSDAY','08:00:00','11:30:00',1),(11,6,'SATURDAY','08:00:00','11:30:00',1),(16,16,'MONDAY','08:00:00','12:00:00',1),(17,16,'WEDNESDAY','08:00:00','12:00:00',1),(18,16,'FRIDAY','08:00:00','12:00:00',1);
/*!40000 ALTER TABLE `doctor_work_shifts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_records`
--

DROP TABLE IF EXISTS `medical_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_records` (
  `appointment_id` bigint NOT NULL,
  `diagnosis` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `clinical_note` varchar(4000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`appointment_id`),
  CONSTRAINT `fk_mr_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_records`
--

LOCK TABLES `medical_records` WRITE;
/*!40000 ALTER TABLE `medical_records` DISABLE KEYS */;
INSERT INTO `medical_records` VALUES (2,'s','s','2026-05-27 08:12:55','2026-05-27 08:12:55');
/*!40000 ALTER TABLE `medical_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(300) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `message` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ref_appointment_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_notifications_user_created` (`user_id`,`created_at`),
  CONSTRAINT `fk_notif_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
INSERT INTO `notifications` (`id`,`user_id`,`type`,`title`,`message`,`ref_appointment_id`,`is_read`,`created_at`) VALUES
(1,17,"APPOINTMENT_CREATED","Đặt lịch thành công","Lịch hẹn với bác sĩ BS. Nguyễn Văn Minh lúc 2026-05-29T08:30 đang chờ xác nhận.",1,0,"2026-05-27 08:06:36"),
(2,17,"APPOINTMENT_CREATED","Đặt lịch thành công","Lịch hẹn với bác sĩ BS. Phạm Thị Thu lúc 2026-06-26T08:30 đang chờ xác nhận.",2,0,"2026-05-27 08:06:58"),
(3,17,"APPOINTMENT_COMPLETED","Khám hoàn thành","Lịch hẹn với bác sĩ BS. Phạm Thị Thu đã hoàn thành.",2,0,"2026-05-27 08:12:13"),
(4,7,"APPOINTMENT_CREATED","Đặt lịch thành công","Lịch hẹn với bác sĩ BS. Phạm Thị Thu lúc 2026-05-29T08:30 đang chờ xác nhận.",3,0,"2026-05-27 08:19:04"),
(5,5,"APPOINTMENT_CREATED","Lịch hẹn mới","Bệnh nhân Nguyễn Thị Hoa đã đặt lịch lúc 2026-05-29T08:30.",3,0,"2026-05-27 08:19:04"),
(6,7,"APPOINTMENT_COMPLETED","Khám hoàn thành","Lịch hẹn với bác sĩ BS. Phạm Thị Thu đã hoàn thành.",3,0,"2026-05-27 08:19:36"),
(7,7,"APPOINTMENT_CREATED","Đặt lịch thành công","Lịch hẹn với bác sĩ BS. Phạm Thị Thu lúc 2026-05-29T08:00 đang chờ xác nhận.",4,0,"2026-05-27 08:24:21"),
(8,5,"APPOINTMENT_CREATED","Lịch hẹn mới","Bệnh nhân Nguyễn Thị Hoa đã đặt lịch lúc 2026-05-29T08:00.",4,0,"2026-05-27 08:24:21"),
(9,7,"APPOINTMENT_CREATED","Đặt lịch thành công","Lịch hẹn với bác sĩ BS. Phạm Thị Thu lúc 2026-05-29T10:00 đang chờ xác nhận.",5,0,"2026-05-27 08:24:32"),
(10,5,"APPOINTMENT_CREATED","Lịch hẹn mới","Bệnh nhân Nguyễn Thị Hoa đã đặt lịch lúc 2026-05-29T10:00.",5,0,"2026-05-27 08:24:32"),
(11,7,"APPOINTMENT_CONFIRMED","Lịch hẹn được xác nhận","Bác sĩ BS. Phạm Thị Thu đã xác nhận lịch hẹn lúc 2026-05-29T08:00.",4,0,"2026-05-27 08:25:05"),
(12,7,"APPOINTMENT_CONFIRMED","Lịch hẹn được xác nhận","Bác sĩ BS. Phạm Thị Thu đã xác nhận lịch hẹn lúc 2026-05-29T10:00.",5,0,"2026-05-27 08:27:20"),
(13,17,"APPOINTMENT_CANCELED","Đã huỷ lịch hẹn","Bạn đã huỷ lịch hẹn lúc 2026-05-29T08:30.",1,0,"2026-05-27 08:41:49"),
(14,17,"APPOINTMENT_CREATED","Đặt lịch thành công","Lịch hẹn với bác sĩ BS. Phạm Thị Thu lúc 2026-05-29T08:30 đang chờ xác nhận.",6,0,"2026-05-27 08:42:03"),
(15,5,"APPOINTMENT_CREATED","Lịch hẹn mới","Bệnh nhân patient đã đặt lịch lúc 2026-05-29T08:30.",6,0,"2026-05-27 08:42:03"),
(16,17,"APPOINTMENT_CONFIRMED","Lịch hẹn được xác nhận","Bác sĩ BS. Phạm Thị Thu đã xác nhận lịch hẹn lúc 2026-05-29T08:30.",6,0,"2026-05-27 08:42:29");
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token` varchar(128) NOT NULL,
  `expires_at` datetime NOT NULL,
  `used_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `idx_password_reset_token` (`token`),
  KEY `fk_prt_user` (`user_id`),
  CONSTRAINT `fk_prt_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescription_items`
--

DROP TABLE IF EXISTS `prescription_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescription_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `prescription_id` bigint NOT NULL,
  `drug_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `dosage` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `frequency` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `instruction` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_pi_prescription` (`prescription_id`),
  CONSTRAINT `fk_pi_prescription` FOREIGN KEY (`prescription_id`) REFERENCES `prescriptions` (`appointment_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescription_items`
--

LOCK TABLES `prescription_items` WRITE;
/*!40000 ALTER TABLE `prescription_items` DISABLE KEYS */;
INSERT INTO `prescription_items` VALUES (1,2,'zzz','203','1lânf/ngày','7 ngày',NULL);
/*!40000 ALTER TABLE `prescription_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescriptions`
--

DROP TABLE IF EXISTS `prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescriptions` (
  `appointment_id` bigint NOT NULL,
  `note` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`appointment_id`),
  CONSTRAINT `fk_prx_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `appointments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescriptions`
--

LOCK TABLES `prescriptions` WRITE;
/*!40000 ALTER TABLE `prescriptions` DISABLE KEYS */;
INSERT INTO `prescriptions` VALUES (2,'s','2026-05-27 08:12:56');
/*!40000 ALTER TABLE `prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specialties`
--

DROP TABLE IF EXISTS `specialties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialties` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `UKbhb8s9o5hv30lkbidtod9cixc` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specialties`
--

LOCK TABLES `specialties` WRITE;
/*!40000 ALTER TABLE `specialties` DISABLE KEYS */;
INSERT INTO `specialties` VALUES (3,'Nhi khoa','Khám và điều trị bệnh lý trẻ em',1,'2026-05-26 19:52:58','2026-05-26 19:52:58'),(29,'Tim mạch','Khám và điều trị các bệnh lý tim mạch',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(30,'Thần kinh','Khám và điều trị các bệnh lý thần kinh',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(31,'Da liễu','Khám và điều trị các bệnh về da',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(32,'Mắt','Khám và điều trị các bệnh lý về mắt',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(33,'Tai mũi họng','Khám và điều trị bệnh lý tai, mũi, họng',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(34,'Cơ xương khớp','Khám và điều trị các bệnh về xương khớp',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(35,'Nội tiết','Tiểu đường, tuyến giáp và các rối loạn nội tiết',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(36,'Tiêu hóa','Khám và điều trị bệnh lý đường tiêu hóa',1,'2026-05-26 19:57:43','2026-05-26 19:57:43'),(37,'Sản phụ khoa','Khám và chăm sóc sức khỏe phụ nữ và thai sản',1,'2026-05-26 19:57:43','2026-05-26 19:57:43');
/*!40000 ALTER TABLE `specialties` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(120) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'ADMIN | DOCTOR | PATIENT',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin@clinic.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','Super Admin',NULL,'ADMIN',1,'2026-05-26 19:52:58','2026-05-26 19:52:58'),(3,'bs_lan','bs.lan@clinic.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','BS. Trần Thị Lan',NULL,'DOCTOR',1,'2026-05-26 19:52:58','2026-05-27 04:19:09'),(4,'bs_hung','bs.hung@clinic.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','BS. Lê Hoàng Hùng',NULL,'DOCTOR',1,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(5,'bs_thu','bs.thu@clinic.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','BS. Phạm Thị Thu',NULL,'DOCTOR',1,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(6,'bs_duc','bs.duc@clinic.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','BS. Hoàng Văn Đức',NULL,'DOCTOR',1,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(7,'patient1','hoa.nguyen@gmail.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','Nguyễn Thị Hoa',NULL,'PATIENT',1,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(8,'patient2','tuan.tran@gmail.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','Trần Minh Tuấn',NULL,'PATIENT',1,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(9,'patient3','mai.le@gmail.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','Lê Thị Mai',NULL,'PATIENT',1,'2026-05-26 19:52:58','2026-05-26 19:57:43'),(16,'bs_minh','bs.minh@clinic.com','$2b$10$aHcO324wdDkcvl2Lo3DljuuHD6CXS6JNBDBjVC9drnU5R0bZjYInu','BS. Nguyễn Văn Minh','0901111111','DOCTOR',1,'2026-05-27 03:56:13','2026-05-27 03:56:13'),(17,'patient','patient@gmail.com','$2a$10$Sn9SMq3Zsmqi1WsY57rY1et38k0Skw3rKWAkWCWXzDXaaDpsOTk7e','patient','0987654321','PATIENT',1,'2026-05-27 07:52:53','2026-05-27 07:52:53');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-27 10:48:45
