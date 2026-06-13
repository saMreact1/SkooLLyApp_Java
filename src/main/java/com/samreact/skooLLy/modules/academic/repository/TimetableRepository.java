package com.samreact.skooLLy.modules.academic.repository;

import com.samreact.skooLLy.modules.academic.entity.enums.DayOfWeek;
import com.samreact.skooLLy.modules.academic.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository
        extends JpaRepository<Timetable, Long> {

    // Get full timetable for a term
    List<Timetable> findAllByTermIdAndSchoolId(Long termId, Long schoolId);

    // Get timetable for a specific classroom in a term
    List<Timetable> findAllByClassroomIdAndTermId(Long classroomId, Long termId);

    // Get timetable for a specific teacher in a term
    List<Timetable> findAllByTeacherIdAndTermId(Long teacherId, Long termId);

    // Get timetable for a specific day
    List<Timetable> findAllByClassroomIdAndTermIdAndDayOfWeek(
            Long classroomId, Long termId,
            DayOfWeek dayOfWeek);

    Optional<Timetable> findByIdAndSchoolId(Long id, Long schoolId);

    // Check for scheduling conflicts
    // Same classroom, same day, overlapping time
    boolean existsByClassroomIdAndTermIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Long classroomId, Long termId,
            DayOfWeek dayOfWeek,
            LocalTime endTime, LocalTime startTime);

    // Check teacher conflict
    boolean existsByTeacherIdAndTermIdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Long teacherId, Long termId,
            DayOfWeek dayOfWeek,
            LocalTime endTime, LocalTime startTime);
}