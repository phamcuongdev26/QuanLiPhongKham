package com.clinic.service.impl;

import com.clinic.constant.Role;
import com.clinic.dto.request.AdminCreateDoctorRequest;
import com.clinic.dto.request.AdminUpdateDoctorRequest;
import com.clinic.dto.response.DoctorSummaryResponse;
import com.clinic.entity.DoctorProfile;
import com.clinic.entity.Specialty;
import com.clinic.entity.User;
import com.clinic.exception.AppException;
import com.clinic.exception.ErrorCode;
import com.clinic.repository.DoctorProfileRepository;
import com.clinic.repository.DoctorProfileRepository.DoctorSummaryProjection;
import com.clinic.repository.SpecialtyRepository;
import com.clinic.repository.UserRepository;
import com.clinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PasswordEncoder passwordEncoder;

    private DoctorSummaryResponse toSummary(DoctorProfile profile) {
        User doctor = profile.getUser();
        return DoctorSummaryResponse.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .email(doctor.getEmail())
                .specialtyId(profile.getSpecialty() == null ? null : profile.getSpecialty().getId())
                .specialtyName(profile.getSpecialty() == null ? null : profile.getSpecialty().getName())
                .consultationFee(profile.getConsultationFee())
                .title(profile.getTitle())
                .bio(profile.getBio())
                .isActive(doctor.isActive())
                .build();
    }

    private DoctorSummaryResponse toSummary(DoctorSummaryProjection doctor) {
        return DoctorSummaryResponse.builder()
                .id(doctor.getId())
                .fullName(doctor.getFullName())
                .email(doctor.getEmail())
                .specialtyId(doctor.getSpecialtyId())
                .specialtyName(doctor.getSpecialtyName())
                .consultationFee(doctor.getConsultationFee())
                .title(doctor.getTitle())
                .bio(doctor.getBio())
                .isActive(Boolean.TRUE.equals(doctor.getIsActive()))
                .build();
    }

    @Override
    public List<DoctorSummaryResponse> listBySpecialty(Long specialtyId) {
        return doctorProfileRepository.findDoctorSummariesBySpecialty(specialtyId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public List<DoctorSummaryResponse> listAll() {
        return doctorProfileRepository.findAllDoctorSummaries().stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    public DoctorSummaryResponse getById(Long doctorId) {
        return doctorProfileRepository.findDoctorSummaryById(doctorId)
                .map(this::toSummary)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
    }

    @Override
    public DoctorSummaryResponse createDoctor(AdminCreateDoctorRequest request) {
        if (userRepository.usernameExists(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.emailExists(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

        User doctorUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .role(Role.DOCTOR)
                .isActive(true)
                .build();
        userRepository.save(doctorUser);

        DoctorProfile profile = DoctorProfile.builder()
                .user(doctorUser)
                .specialty(specialty)
                .title(request.getTitle())
                .bio(request.getBio())
                .consultationFee(request.getConsultationFee())
                .build();
        doctorProfileRepository.save(profile);

        return toSummary(profile);
    }

    @Override
    public DoctorSummaryResponse updateDoctor(Long doctorUserId, AdminUpdateDoctorRequest request) {
        User user = userRepository.findById(doctorUserId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        if (user.getRole() != Role.DOCTOR)
            throw new AppException(ErrorCode.DOCTOR_NOT_FOUND);

        Specialty specialty = specialtyRepository.findById(request.getSpecialtyId())
                .orElseThrow(() -> new AppException(ErrorCode.SPECIALTY_NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        if (request.getIsActive() != null) user.setActive(request.getIsActive());
        userRepository.save(user);

        DoctorProfile profile = doctorProfileRepository.findById(doctorUserId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        profile.setSpecialty(specialty);
        profile.setTitle(request.getTitle());
        profile.setBio(request.getBio());
        profile.setConsultationFee(request.getConsultationFee());
        return toSummary(doctorProfileRepository.save(profile));
    }

    @Override
    public void deleteDoctor(Long doctorUserId) {
        User user = userRepository.findById(doctorUserId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));
        if (user.getRole() != Role.DOCTOR) {
            throw new AppException(ErrorCode.DOCTOR_NOT_FOUND);
        }
        user.setActive(false);
        userRepository.save(user);
    }
}
