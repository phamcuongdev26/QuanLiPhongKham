package com.clinic.repository;

import com.clinic.constant.Role;
import com.clinic.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users WHERE username = :username LIMIT 1", nativeQuery = true)
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM users WHERE email = :email LIMIT 1", nativeQuery = true)
    Optional<User> findByEmail(String email);

    @Query(value = """
            SELECT *
            FROM users
            WHERE username = :username OR email = :email
            LIMIT 1
            """, nativeQuery = true)
    Optional<User> findByLogin(@Param("username") String username,
                               @Param("email") String email);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM users WHERE username = :username)", nativeQuery = true)
    boolean usernameExists(@Param("username") String username);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM users WHERE email = :email)", nativeQuery = true)
    boolean emailExists(@Param("email") String email);

    @Query(value = "SELECT * FROM users WHERE role = :role ORDER BY full_name ASC", nativeQuery = true)
    List<User> findUsersByRole(@Param("role") Role role);

    @Query(value = "SELECT COUNT(*) FROM users WHERE role = :role", nativeQuery = true)
    long countUsersByRole(@Param("role") Role role);

    @Query(value = "SELECT * FROM users ORDER BY created_at DESC LIMIT 5", nativeQuery = true)
    List<User> findLatestFiveUsers();

    @Query(value = "SELECT COUNT(*) FROM users WHERE role != :role AND created_at >= :from AND created_at < :to",
           nativeQuery = true)
    long countNonAdminBetween(@Param("role") String role,
                              @Param("from") LocalDateTime from,
                              @Param("to") LocalDateTime to);

    @Query(value = """
            SELECT * FROM users
            WHERE (:q IS NULL
                   OR LOWER(username) LIKE CONCAT('%', LOWER(:q), '%')
                   OR LOWER(email) LIKE CONCAT('%', LOWER(:q), '%')
                   OR LOWER(full_name) LIKE CONCAT('%', LOWER(:q), '%'))
              AND (:role IS NULL OR role = :role)
              AND (:activeFilter IS NULL OR is_active = :activeFilter)
            ORDER BY created_at DESC
            """,
           countQuery = """
            SELECT COUNT(*) FROM users
            WHERE (:q IS NULL
                   OR LOWER(username) LIKE CONCAT('%', LOWER(:q), '%')
                   OR LOWER(email) LIKE CONCAT('%', LOWER(:q), '%')
                   OR LOWER(full_name) LIKE CONCAT('%', LOWER(:q), '%'))
              AND (:role IS NULL OR role = :role)
              AND (:activeFilter IS NULL OR is_active = :activeFilter)
            """,
           nativeQuery = true)
    Page<User> findFiltered(@Param("q") String q,
                            @Param("role") String role,
                            @Param("activeFilter") Boolean activeFilter,
                            Pageable pageable);
}
