package com.samreact.skooLLy.modules.school.entity;

import com.samreact.skooLLy.modules.school.entity.enums.SchoolStatus;
import com.samreact.skooLLy.modules.school.entity.enums.SchoolType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "schools")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

//    @Column(nullable = false)
//    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SchoolType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SchoolStatus status = SchoolStatus.PENDING;

    @Column(name = "logo_url", columnDefinition = "TEXT")
    private String logoUrl;

    // Unique school identifier used for scoping
    @Column(nullable = false, unique = true)
    private String schoolCode;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    // Audit
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;
}