package com.clinic.service.impl;

import com.clinic.constant.AppointmentStatus;
import com.clinic.constant.NotificationType;
import com.clinic.constant.Role;
import com.clinic.dto.request.CreateAppointmentRequest;
import com.clinic.dto.request.UpdateAppointmentStatusRequest;
import com.clinic.dto.response.AppointmentResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.Specialty;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.SpecialtyRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.AppointmentService;
import com.clinic.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final NotificationService notificationService;

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getFullName())
                .doctorId(a.getDoctor().getId())
                .doctorName(a.getDoctor().getFullName())
                .specialtyId(a.getSpecialty() == null ? null : a.getSpecialty().getId())
                .specialtyName(a.getSpecialty() == null ? null : a.getSpecialty().getName())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .symptomDescription(a.getSymptomDescription())
                .status(a.getStatus())
                .doctorNote(a.getDoctorNote())
                .build();
    }

    private User loadByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    @Override
    @Transactional
    public AppointmentResponse createForPatient(String patientUsername, CreateAppointmentRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        User patient = loadByUsername(patientUsername);
        if (patient.getRole() != Role.PATIENT) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        if (doctor.getRole() != Role.DOCTOR) {
            throw new AppException(ErrorCode.DOCTOR_NOT_FOUND);
        }

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

        boolean overlapping = appointmentRepository.existsOverlappingDoctorAppointment(
                doctor.getId(),
                request.getStartTime(),
                request.getEndTime(),
                List.of(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED)
        );
        if (overlapping) {
            throw new AppException(ErrorCode.APPOINTMENT_TIME_UNAVAILABLE);
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .specialty(specialty)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .symptomDescription(request.getSymptomDescription())
                .status(AppointmentStatus.PENDING)
                .build();
        Appointment saved = appointmentRepository.save(appointment);
        notificationService.send(patient, NotificationType.APPOINTMENT_CREATED,
                "Đặt lịch thành công",
                "Lịch hẹn với bác sĩ " + doctor.getFullName() + " lúc " + saved.getStartTime() + " đang chờ xác nhận.",
                saved.getId());
        return toResponse(saved);
    }

    @Override
    public List<AppointmentResponse> listForPatient(String patientUsername) {
        User patient = loadByUsername(patientUsername);
        return appointmentRepository.findByPatient_IdOrderByStartTimeDesc(patient.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void cancelByPatient(String patientUsername, Long appointmentId) {
        User patient = loadByUsername(patientUsername);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID_STATUS);
        }
        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
        notificationService.send(patient, NotificationType.APPOINTMENT_CANCELED,
                "Đã huỷ lịch hẹn",
                "Bạn đã huỷ lịch hẹn lúc " + appointment.getStartTime() + ".",
                appointment.getId());
    }

    @Override
    public List<AppointmentResponse> listDoctorToday(String doctorUsername, LocalDate date) {
        User doctor = loadByUsername(doctorUsername);
        LocalDate target = date == null ? LocalDate.now() : date;
        LocalDateTime from = target.atStartOfDay();
        LocalDateTime to = target.atTime(LocalTime.MAX);
        return appointmentRepository.findByDoctor_IdAndStartTimeBetweenOrderByStartTimeAsc(doctor.getId(), from, to).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse updateDoctorStatus(String doctorUsername, Long appointmentId, UpdateAppointmentStatusRequest request) {
        User doctor = loadByUsername(doctorUsername);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        AppointmentStatus next = request.getStatus();
        if (next == AppointmentStatus.CANCELED || next == AppointmentStatus.PENDING) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID_STATUS);
        }
        appointment.setStatus(next);
        appointment.setDoctorNote(request.getDoctorNote());
        AppointmentResponse response = toResponse(appointmentRepository.save(appointment));

        User patient = appointment.getPatient();
        String doctorName = doctor.getFullName();
        switch (next) {
            case CONFIRMED -> notificationService.send(patient, NotificationType.APPOINTMENT_CONFIRMED,
                    "Lịch hẹn được xác nhận",
                    "Bác sĩ " + doctorName + " đã xác nhận lịch hẹn lúc " + appointment.getStartTime() + ".",
                    appointment.getId());
            case REJECTED -> notificationService.send(patient, NotificationType.APPOINTMENT_REJECTED,
                    "Lịch hẹn bị từ chối",
                    "Bác sĩ " + doctorName + " đã từ chối lịch hẹn lúc " + appointment.getStartTime()
                            + (request.getDoctorNote() != null ? ". Lý do: " + request.getDoctorNote() : "") + ".",
                    appointment.getId());
            case COMPLETED -> notificationService.send(patient, NotificationType.APPOINTMENT_COMPLETED,
                    "Khám hoàn thành",
                    "Lịch hẹn với bác sĩ " + doctorName + " đã hoàn thành.",
                    appointment.getId());
            default -> { }
        }
        return response;
    }
}

