package com.samreact.skooLLy.modules.academic.service;

import com.samreact.skooLLy.modules.academic.dto.*;

import java.util.List;

public interface AcademicService {
    SessionResponse createSession(CreateSessionRequest request);

    SessionResponse getSessionById(Long id);

    List<SessionResponse> getAllSessions();

    SessionResponse setCurrentSession(Long id);

    void deleteSession(Long id);

    TermResponse createTerm(CreateTermRequest request);

    TermResponse getTermById(Long id);

    List<TermResponse> getTermsBySession(Long sessionId);

    TermResponse updateTerm(Long id, UpdateTermRequest request);

    TermResponse setCurrentTerm(Long id);

    void deleteTerm(Long id);

    SessionResponse getCurrentSession();

    TermResponse getCurrentTerm();

    SubjectResponse createSubject(CreateSubjectRequest request);

    SubjectResponse getSubjectById(Long id);

    List<SubjectResponse> getAllSubjects();

    List<SubjectResponse> getElectiveSubjects();

    SubjectResponse updateSubject(Long id, UpdateSubjectRequest request);

    void deleteSubject(Long id);

    ClassroomResponse createClassroom(CreateClassroomRequest request);

    ClassroomResponse getClassroomById(Long id);

    List<ClassroomResponse> getAllClassrooms();

    List<ClassroomResponse> getClassroomsByLevel(String level);

    ClassroomResponse updateClassroom(Long id, UpdateClassroomRequest request);

    void deleteClassroom(Long id);

    long getClassroomCount();

    TimetableResponse createTimetableEntry(CreateTimetableRequest request);

    List<TimetableResponse> getTimetableByTerm(Long termId);

    List<TimetableResponse> getTimetableByClassroom(Long classroomId, Long termId);

    List<TimetableResponse> getTimetableByTeacher(Long teacherId, Long termId);

    void deleteTimetableEntry(Long id);
}