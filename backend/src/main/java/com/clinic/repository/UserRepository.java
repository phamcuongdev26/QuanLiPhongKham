package com.clinic.repository;

import com.clinic.constant.Role;
import com.clinic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    long countByRole(Role role);

    List<User> findTop5ByOrderByCreatedAtDesc();

    @Query(value = "SELECT COUNT(*) FROM users WHERE role != :role AND created_at >= :from AND created_at < :to",
           nativeQuery = true)
    long countNonAdminBetween(@Param("role") String role,
                              @Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);
}
