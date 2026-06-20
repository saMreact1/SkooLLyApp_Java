package com.samreact.skooLLy.modules.grades.entity;

import com.samreact.skooLLy.modules.school.entity.School;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grading_scales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GradingScale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(nullable = false)
    private String gradeLetter;

    @Column(nullable = false)
    private BigDecimal minPercentage;

    @Column(nullable = false)
    private BigDecimal maxPercentage;

    @Column
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private boolean deleted = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;
}
