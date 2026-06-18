package com.samreact.skooLLy.modules.user.repository;

import com.samreact.skooLLy.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Finds a user by their email
    // Spring generates: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Checks if a user exists with the given email
    // Spring generates: SELECT COUNT(*) FROM users WHERE email = ?
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByEmailAndDeleted(String email, boolean deleted);

    List<User> findBySchoolIdAndDeleted(Long schoolId, boolean deleted);

    Page<User> findBySchoolIdAndDeleted(Long schoolId, boolean deleted, Pageable pageable);

    Optional<User> findByPhoneNumberAndDeleted(String phoneNumber, boolean deleted);

    @Query("""
            SELECT u FROM User u
            WHERE u.school.id = :schoolId
              AND u.deleted = false
              AND u.id <> :currentUserId
              AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY u.firstName ASC
            """)
    List<User> searchByQuery(@Param("schoolId") Long schoolId,
                             @Param("currentUserId") Long currentUserId,
                             @Param("query") String query);

    @Query("SELECT u FROM User u WHERE u.school.id = :schoolId AND u.id != :currentUserId AND u.deleted = false " +
           "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchByQuery(@Param("schoolId") Long schoolId,
                             @Param("currentUserId") Long currentUserId,
                             @Param("query") String query,
                             Pageable pageable);

    @Query("SELECT u.school.id FROM User u WHERE u.email = :email AND u.deleted = false")
    Long getSchoolIdByEmail(@Param("email") String email);
}
