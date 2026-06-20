package com.samreact.skooLLy.modules.grades.entity;

import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"exam_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private BigDecimal marksObtained;

    @Column
    private String letterGrade;

    @Column
    private String remark;

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
