package com.clinic.configuration;

import com.clinic.constant.Role;
import com.clinic.entity.DoctorProfile;
import com.clinic.entity.DoctorWorkShift;
import com.clinic.entity.Specialty;
import com.clinic.entity.User;
import com.clinic.repository.DoctorProfileRepository;
import com.clinic.repository.DoctorWorkShiftRepository;
import com.clinic.repository.SpecialtyRepository;
import com.clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final DoctorWorkShiftRepository doctorWorkShiftRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedSpecialties();
        seedAdmin();
        seedDoctors();
        seedPatients();
    }

    private void seedSpecialties() {
        List<String[]> specs = List.of(
            new String[]{"Tim mạch",      "Khám và điều trị các bệnh lý tim mạch"},
            new String[]{"Thần kinh",     "Khám và điều trị các bệnh lý thần kinh"},
            new String[]{"Nhi khoa",      "Khám và điều trị bệnh lý trẻ em"},
            new String[]{"Da liễu",       "Khám và điều trị các bệnh về da"},
            new String[]{"Mắt",           "Khám và điều trị các bệnh lý về mắt"},
            new String[]{"Tai mũi họng",  "Khám và điều trị bệnh lý tai, mũi, họng"},
            new String[]{"Cơ xương khớp", "Khám và điều trị các bệnh về xương khớp"},
            new String[]{"Nội tiết",      "Tiểu đường, tuyến giáp và các rối loạn nội tiết"},
            new String[]{"Tiêu hóa",      "Khám và điều trị bệnh lý đường tiêu hóa"},
            new String[]{"Sản phụ khoa",  "Khám và chăm sóc sức khỏe phụ nữ và thai sản"}
        );
        for (String[] s : specs) {
            if (specialtyRepository.findByNameIgnoreCase(s[0]).isEmpty()) {
                specialtyRepository.save(Specialty.builder()
                    .name(s[0]).description(s[1]).isActive(true).build());
            }
        }
    }

    private void seedAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                .username("admin")
                .email("admin@clinic.com")
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .fullName("Super Admin")
                .role(Role.ADMIN)
                .isActive(true)
                .build());
        }
    }

    private void seedDoctors() {
        record DoctorInfo(String username, String email, String fullName, String phone,
                          String specialtyName, String title, String bio, long fee,
                          List<DayOfWeek> days, LocalTime start, LocalTime end) {}

        List<DoctorInfo> doctors = List.of(
            new DoctorInfo("bs_minh", "bs.minh@clinic.com", "BS. Nguyễn Văn Minh", "0901111111",
                "Tim mạch", "Tiến sĩ, Bác sĩ CKI",
                "Hơn 10 năm kinh nghiệm điều trị bệnh lý tim mạch tại các bệnh viện lớn.", 300000L,
                List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                LocalTime.of(8, 0), LocalTime.of(12, 0)),

            new DoctorInfo("bs_lan", "bs.lan@clinic.com", "BS. Trần Thị Lan", "0901111112",
                "Thần kinh", "Thạc sĩ, Bác sĩ CKII",
                "Chuyên gia đầu ngành về các bệnh đột quỵ và rối loạn thần kinh.", 350000L,
                List.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                LocalTime.of(8, 0), LocalTime.of(11, 30)),

            new DoctorInfo("bs_hung", "bs.hung@clinic.com", "BS. Lê Hoàng Hùng", "0901111113",
                "Nhi khoa", "Bác sĩ CKI",
                "Bác sĩ chuyên khoa nhi, nhiều kinh nghiệm điều trị bệnh trẻ em.", 200000L,
                List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                LocalTime.of(13, 0), LocalTime.of(17, 0)),

            new DoctorInfo("bs_thu", "bs.thu@clinic.com", "BS. Phạm Thị Thu", "0901111114",
                "Da liễu", "Thạc sĩ Y khoa",
                "Chuyên điều trị các bệnh da mãn tính, dị ứng và chăm sóc da.", 250000L,
                List.of(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY),
                LocalTime.of(8, 0), LocalTime.of(12, 0)),

            new DoctorInfo("bs_duc", "bs.duc@clinic.com", "BS. Hoàng Văn Đức", "0901111115",
                "Mắt", "Bác sĩ CKII",
                "Phẫu thuật mắt, điều trị đục thủy tinh thể và các bệnh về mắt.", 280000L,
                List.of(DayOfWeek.THURSDAY, DayOfWeek.SATURDAY),
                LocalTime.of(8, 0), LocalTime.of(11, 30))
        );

        for (DoctorInfo d : doctors) {
            if (userRepository.existsByUsername(d.username())) continue;

            User user = userRepository.save(User.builder()
                .username(d.username())
                .email(d.email())
                .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                .fullName(d.fullName())
                .phoneNumber(d.phone())
                .role(Role.DOCTOR)
                .isActive(true)
                .build());

            Specialty specialty = specialtyRepository.findByNameIgnoreCase(d.specialtyName()).orElse(null);

            doctorProfileRepository.save(DoctorProfile.builder()
                .user(user)
                .specialty(specialty)
                .title(d.title())
                .bio(d.bio())
                .consultationFee(d.fee())
                .build());

            for (DayOfWeek day : d.days()) {
                doctorWorkShiftRepository.save(DoctorWorkShift.builder()
                    .doctor(user)
                    .dayOfWeek(day)
                    .startTime(d.start())
                    .endTime(d.end())
                    .isActive(true)
                    .build());
            }
        }
    }

    private void seedPatients() {
        List<String[]> patients = List.of(
            new String[]{"patient1", "hoa.nguyen@gmail.com",  "Nguyễn Thị Hoa",  "0912000001"},
            new String[]{"patient2", "tuan.tran@gmail.com",   "Trần Minh Tuấn",   "0912000002"},
            new String[]{"patient3", "mai.le@gmail.com",      "Lê Thị Mai",       "0912000003"}
        );
        for (String[] p : patients) {
            if (!userRepository.existsByUsername(p[0])) {
                userRepository.save(User.builder()
                    .username(p[0])
                    .email(p[1])
                    .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .fullName(p[2])
                    .phoneNumber(p[3])
                    .role(Role.PATIENT)
                    .isActive(true)
                    .build());
            }
        }
    }
}
