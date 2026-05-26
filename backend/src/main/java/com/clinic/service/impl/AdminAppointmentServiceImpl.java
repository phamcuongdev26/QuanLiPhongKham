package com.clinic.service.impl;

import com.clinic.constant.AppointmentStatus;
import com.clinic.constant.NotificationType;
import com.clinic.dto.request.UpdateAppointmentStatusRequest;
import com.clinic.dto.response.AppointmentResponse;
import com.clinic.entity.Appointment;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.AppointmentRepository;
import com.clinic.service.AdminAppointmentService;
import com.clinic.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminAppointmentServiceImpl implements AdminAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    // State machine: defines which transitions are allowed per current status
    private static final Map<AppointmentStatus, Set<AppointmentStatus>> ALLOWED_TRANSITIONS = Map.of(
            AppointmentStatus.PENDING,   EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.REJECTED, AppointmentStatus.CANCELED),
            AppointmentStatus.CONFIRMED, EnumSet.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELED),
            AppointmentStatus.REJECTED,  EnumSet.noneOf(AppointmentStatus.class),
            AppointmentStatus.CANCELED,  EnumSet.noneOf(AppointmentStatus.class),
            AppointmentStatus.COMPLETED, EnumSet.noneOf(AppointmentStatus.class)
    );

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

    @Override
    public List<AppointmentResponse> listAll() {
        return appointmentRepository.findAll().stream()
                .sorted((a, b) -> b.getStartTime().compareTo(a.getStartTime()))
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatus(Long id, UpdateAppointmentStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        AppointmentStatus current = appointment.getStatus();
        AppointmentStatus next = request.getStatus();

        Set<AppointmentStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(AppointmentStatus.class));
        if (!allowed.contains(next)) {
            throw new AppException(ErrorCode.APPOINTMENT_INVALID_STATUS);
        }

        appointment.setStatus(next);
        if (request.getDoctorNote() != null) {
            appointment.setDoctorNote(request.getDoctorNote());
        }
        Appointment saved = appointmentRepository.save(appointment);

        notifyPatient(saved, next, request.getDoctorNote());
        return toResponse(saved);
    }

    private void notifyPatient(Appointment appointment, AppointmentStatus next, String note) {
        User patient = appointment.getPatient();
        String doctorName = appointment.getDoctor().getFullName();
        switch (next) {
            case CONFIRMED -> notificationService.send(patient, NotificationType.APPOINTMENT_CONFIRMED,
                    "Lịch hẹn được xác nhận",
                    "Lịch hẹn với bác sĩ " + doctorName + " lúc " + appointment.getStartTime() + " đã được xác nhận.",
                    appointment.getId());
            case REJECTED -> notificationService.send(patient, NotificationType.APPOINTMENT_REJECTED,
                    "Lịch hẹn bị từ chối",
                    "Lịch hẹn với bác sĩ " + doctorName + " lúc " + appointment.getStartTime()
                            + (note != null ? ". Lý do: " + note : "") + ".",
                    appointment.getId());
            case CANCELED -> notificationService.send(patient, NotificationType.APPOINTMENT_CANCELED,
                    "Lịch hẹn bị hủy",
                    "Lịch hẹn với bác sĩ " + doctorName + " lúc " + appointment.getStartTime() + " đã bị hủy bởi quản trị viên.",
                    appointment.getId());
            case COMPLETED -> notificationService.send(patient, NotificationType.APPOINTMENT_COMPLETED,
                    "Khám hoàn thành",
                    "Lịch hẹn với bác sĩ " + doctorName + " đã hoàn thành.",
                    appointment.getId());
            default -> { }
        }
    }
}
