package com.clinic.service.impl;

import com.clinic.constant.AppointmentStatus;
import com.clinic.constant.Role;
import com.clinic.entity.DoctorProfile;
import com.clinic.entity.User;
import com.clinic.repository.DoctorProfileRepository;
import com.clinic.repository.AppointmentRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    @Override
    public Map<String, Object> overview() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalAppointments", appointmentRepository.count());
        result.put("pendingAppointments", appointmentRepository.countByStatus(AppointmentStatus.PENDING));
        result.put("confirmedAppointments", appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED));
        result.put("completedAppointments", appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
        result.put("totalDoctors", userRepository.countByRole(Role.DOCTOR));
        result.put("totalPatients", userRepository.countByRole(Role.PATIENT));

        long revenue = appointmentRepository.sumRevenueByCompletedStatus(AppointmentStatus.COMPLETED);
        result.put("revenue", revenue);

        List<Long> doctorIds = appointmentRepository.findDoctorIdsByCompletedCountDesc(AppointmentStatus.COMPLETED);
        if (!doctorIds.isEmpty()) {
            Long busiestDoctorId = doctorIds.get(0);
            User doctor = userRepository.findById(busiestDoctorId).orElse(null);
            DoctorProfile profile = doctorProfileRepository.findById(busiestDoctorId).orElse(null);
            Map<String, Object> busiestDoctor = new HashMap<>();
            busiestDoctor.put("id", busiestDoctorId);
            busiestDoctor.put("fullName", doctor == null ? null : doctor.getFullName());
            busiestDoctor.put("email", doctor == null ? null : doctor.getEmail());
            busiestDoctor.put("specialty", profile == null || profile.getSpecialty() == null ? null : profile.getSpecialty().getName());
            result.put("busiestDoctor", busiestDoctor);
        } else {
            result.put("busiestDoctor", null);
        }
        return result;
    }
}
