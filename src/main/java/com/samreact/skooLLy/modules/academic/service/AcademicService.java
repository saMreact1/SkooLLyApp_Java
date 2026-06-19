package com.samreact.skooLLy.modules.academic.service;

import com.samreact.skooLLy.common.response.PagedResponse;
import com.samreact.skooLLy.modules.academic.dto.*;

import java.util.List;

public interface AcademicService {
    SessionResponse createSession(CreateSessionRequest request);

    SessionResponse getSessionById(Long id);

    PagedResponse<SessionResponse> getAllSessions(int page, int size);

    SessionResponse setCurrentSession(Long id);

    void deleteSession(Long id);

    TermResponse createTerm(CreateTermRequest request);

    TermResponse getTermById(Long id);

    List<TermResponse> getTermsBySession(Long sessionId);

    TermResponse setCurrentTerm(Long id);

    void deleteTerm(Long id);

    SessionResponse getCurrentSession();

    TermResponse getCurrentTerm();

    SubjectResponse createSubject(CreateSubjectRequest request);

    SubjectResponse getSubjectById(Long id);

    PagedResponse<SubjectResponse> getAllSubjects(int page, int size);

    List<SubjectResponse> getElectiveSubjects();

    SubjectResponse updateSubject(Long id, UpdateSubjectRequest request);

    void deleteSubject(Long id);

    ClassroomResponse createClassroom(CreateClassroomRequest request);

    ClassroomResponse getClassroomById(Long id);

    PagedResponse<ClassroomResponse> getAllClassrooms(int page, int size);

    List<ClassroomResponse> getClassroomsByLevel(String level);

    ClassroomResponse updateClassroom(Long id, UpdateClassroomRequest request);

    void deleteClassroom(Long id);

    long getClassroomCount();

    TimetableResponse createTimetableEntry(CreateTimetableRequest request);

    List<TimetableResponse> getTimetableByTerm(Long termId);

    List<TimetableResponse> getTimetableByClassroom(Long classroomId, Long termId);

    List<TimetableResponse> getTimetableByTeacher(Long teacherId, Long termId);

    void deleteTimetableEntry(Long id);

    TimetableResponse updateTimetableEntry(Long id, UpdateTimetableRequest request);

    List<StudentSubjectResponse> enrollStudent(EnrollStudentRequest request);

    List<StudentSubjectResponse> enrollMe(List<Long> subjectIds, Long termId);

    void dropMySubject(Long subjectId, Long termId);

    void dropStudentFromSubject(Long studentId, Long subjectId, Long termId);

    List<StudentSubjectResponse> getMyStudentSubjects(Long termId);

    List<StudentSubjectResponse> getStudentSubjects(Long studentId, Long termId);

    List<EnrolledStudentResponse> getSubjectStudents(Long subjectId, Long termId);
}