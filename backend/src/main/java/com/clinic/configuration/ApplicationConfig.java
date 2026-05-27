package com.clinic.configuration;

import com.clinic.constant.Role;
import com.clinic.entity.Specialty;
import com.clinic.entity.User;
import com.clinic.repository.SpecialtyRepository;
import com.clinic.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@Slf4j
public class ApplicationConfig {

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository,
                                        SpecialtyRepository specialtyRepository,
                                        PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@QLPB.com").isEmpty()
                    && userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .fullName("System Admin")
                        .email("admin@clinic.local")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .build();
                userRepository.save(admin);
                log.warn("==> Default admin: admin / Admin@123");
            }

            if (specialtyRepository.count() == 0) {
                specialtyRepository.saveAll(List.of(
                        Specialty.builder().name("Nội tổng quát").description("Khám tổng quát").isActive(true).build(),
                        Specialty.builder().name("Tim mạch").description("Khám tim mạch").isActive(true).build(),
                        Specialty.builder().name("Da liễu").description("Khám da liễu").isActive(true).build()
                ));
                log.info("==> Seeded specialties");
            }
        };
    }
}

