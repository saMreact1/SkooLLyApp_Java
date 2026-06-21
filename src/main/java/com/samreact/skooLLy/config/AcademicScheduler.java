package com.samreact.skooLLy.config;

import com.samreact.skooLLy.modules.academic.entity.AcademicSession;
import com.samreact.skooLLy.modules.academic.entity.Term;
import com.samreact.skooLLy.modules.academic.entity.enums.SessionStatus;
import com.samreact.skooLLy.modules.academic.entity.enums.TermStatus;
import com.samreact.skooLLy.modules.academic.repository.AcademicSessionRepository;
import com.samreact.skooLLy.modules.academic.repository.TermRepository;
import com.samreact.skooLLy.modules.school.entity.School;
import com.samreact.skooLLy.modules.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcademicScheduler {

    private static final String[] TERM_NAMES = {"First Term", "Second Term", "Third Term"};

    private final SchoolRepository schoolRepository;
    private final AcademicSessionRepository sessionRepository;
    private final TermRepository termRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationStartup() {
        log.info("Running academic period rollover check on application startup...");
        rolloverAcademicPeriods();
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void rolloverAcademicPeriods() {
        log.info("Starting daily academic period rollover check...");
        List<School> schools = schoolRepository.findAll();

        for (School school : schools) {
            try {
                rolloverTermsForSchool(school);
                rolloverSessionForSchool(school);
            } catch (Exception e) {
                log.error("Error during rollover for school {}: {}", school.getId(), e.getMessage(), e);
            }
        }
        log.info("Academic period rollover check complete.");
    }

    private void rolloverTermsForSchool(School school) {
        Long schoolId = school.getId();
        Optional<Term> currentTermOpt = termRepository.findBySchoolIdAndCurrentTrue(schoolId);

        if (currentTermOpt.isEmpty()) {
            log.debug("No current term for school {}, checking if we should activate one", schoolId);
            activateFirstTermForSchool(school);
            return;
        }

        Term currentTerm = currentTermOpt.get();
        LocalDate today = LocalDate.now();

        if (today.isAfter(currentTerm.getEndDate())) {
            Optional<Term> nextTerm = termRepository
                    .findFirstBySessionIdAndStartDateAfterOrderByStartDateAsc(
                            currentTerm.getSession().getId(), currentTerm.getEndDate());

            if (nextTerm.isPresent()) {
                log.info("Current term '{}' for school {} has ended, rolling over to '{}'",
                        currentTerm.getName(), schoolId, nextTerm.get().getName());

                currentTerm.setCurrent(false);
                currentTerm.setStatus(TermStatus.COMPLETED);
                termRepository.save(currentTerm);

                Term term = nextTerm.get();
                term.setCurrent(true);
                term.setStatus(TermStatus.ACTIVE);
                termRepository.save(term);
                log.info("Activated next term '{}' for school {}", term.getName(), schoolId);
            } else {
                long termCount = termRepository.countBySessionId(currentTerm.getSession().getId());

                if (termCount < 3) {
                    log.info("Current term '{}' for school {} ended, only {} terms exist. Auto-creating next term",
                            currentTerm.getName(), schoolId, termCount);

                    currentTerm.setCurrent(false);
                    currentTerm.setStatus(TermStatus.COMPLETED);
                    termRepository.save(currentTerm);

                    String nextTermName = TERM_NAMES[(int) termCount];
                    Term newTerm = Term.builder()
                            .school(school)
                            .session(currentTerm.getSession())
                            .name(nextTermName)
                            .startDate(currentTerm.getEndDate().plusDays(1))
                            .endDate(currentTerm.getEndDate().plusMonths(3))
                            .status(TermStatus.UPCOMING)
                            .current(true)
                            .needsDateUpdate(true)
                            .build();
                    termRepository.save(newTerm);
                    log.info("Auto-created term '{}' for school {} (needs date update)", nextTermName, schoolId);
                } else {
                    log.info("Current term '{}' for school {} ended, all 3 terms complete. Session rollover will handle",
                            currentTerm.getName(), schoolId);
                }
            }
        } else if (today.isBefore(currentTerm.getStartDate())) {
            log.debug("Current term '{}' hasn't started yet (starts {}), skipping",
                    currentTerm.getName(), currentTerm.getStartDate());
        }
    }

    private void activateFirstTermForSchool(School school) {
        Long schoolId = school.getId();
        Optional<AcademicSession> currentSessionOpt = sessionRepository.findBySchoolIdAndCurrentTrue(schoolId);

        if (currentSessionOpt.isEmpty()) {
            return;
        }

        AcademicSession session = currentSessionOpt.get();
        LocalDate today = LocalDate.now();

        if (today.isBefore(session.getStartDate()) || today.isAfter(session.getEndDate())) {
            return;
        }

        List<Term> terms = termRepository.findAllBySessionIdAndSchoolIdOrderByStartDateAsc(
                session.getId(), schoolId);

        for (Term term : terms) {
            if (!today.isBefore(term.getStartDate()) && !today.isAfter(term.getEndDate())) {
                term.setCurrent(true);
                term.setStatus(TermStatus.ACTIVE);
                termRepository.save(term);
                log.info("Activated term '{}' for school {} (session {})",
                        term.getName(), schoolId, session.getName());
                return;
            }
        }

        for (Term term : terms) {
            if (today.isBefore(term.getStartDate())) {
                term.setCurrent(true);
                term.setStatus(TermStatus.UPCOMING);
                termRepository.save(term);
                log.info("Set upcoming term '{}' as current for school {}", term.getName(), schoolId);
                return;
            }
        }
    }

    private void rolloverSessionForSchool(School school) {
        Long schoolId = school.getId();
        Optional<AcademicSession> currentSessionOpt = sessionRepository.findBySchoolIdAndCurrentTrue(schoolId);

        if (currentSessionOpt.isEmpty()) {
            log.debug("No current session for school {}, checking if we should activate one", schoolId);
            activateFirstSessionForSchool(school);
            return;
        }

        AcademicSession currentSession = currentSessionOpt.get();
        LocalDate today = LocalDate.now();

        Optional<Term> currentTermOpt = termRepository.findBySchoolIdAndCurrentTrue(schoolId);
        if (currentTermOpt.isPresent()) {
            log.debug("Current session '{}' for school {} still has an active term, skipping session rollover",
                    currentSession.getName(), schoolId);
            return;
        }

        if (today.isAfter(currentSession.getEndDate())) {
            Optional<AcademicSession> nextSession = sessionRepository
                    .findFirstBySchoolIdAndStartDateAfterOrderByStartDateAsc(
                            schoolId, currentSession.getEndDate());

            if (nextSession.isPresent()) {
                log.info("Current session '{}' for school {} has ended, rolling over to '{}'",
                        currentSession.getName(), schoolId, nextSession.get().getName());

                currentSession.setCurrent(false);
                currentSession.setStatus(SessionStatus.COMPLETED);
                sessionRepository.save(currentSession);

                AcademicSession session = nextSession.get();
                session.setCurrent(true);
                session.setStatus(SessionStatus.ACTIVE);
                sessionRepository.save(session);
                log.info("Activated next session '{}' for school {}", session.getName(), schoolId);

                activateFirstTermForSession(school, session);
            } else {
                log.info("Current session '{}' for school {} ended. Auto-creating next session",
                        currentSession.getName(), schoolId);

                currentSession.setCurrent(false);
                currentSession.setStatus(SessionStatus.COMPLETED);
                sessionRepository.save(currentSession);

                String nextSessionName = deriveNextSessionName(currentSession.getName());
                AcademicSession newSession = AcademicSession.builder()
                        .school(school)
                        .name(nextSessionName)
                        .startDate(currentSession.getEndDate().plusDays(1))
                        .endDate(currentSession.getEndDate().plusYears(1))
                        .status(SessionStatus.UPCOMING)
                        .current(true)
                        .needsDateUpdate(true)
                        .build();
                sessionRepository.save(newSession);
                log.info("Auto-created session '{}' for school {} (needs date update)", nextSessionName, schoolId);

                Term firstTerm = Term.builder()
                        .school(school)
                        .session(newSession)
                        .name(TERM_NAMES[0])
                        .startDate(newSession.getStartDate())
                        .endDate(newSession.getStartDate().plusMonths(3))
                        .status(TermStatus.UPCOMING)
                        .current(true)
                        .needsDateUpdate(true)
                        .build();
                termRepository.save(firstTerm);
                log.info("Auto-created 'First Term' in session '{}' for school {} (needs date update)",
                        nextSessionName, schoolId);
            }
        }
    }

    private void activateFirstSessionForSchool(School school) {
        Long schoolId = school.getId();
        LocalDate today = LocalDate.now();

        List<AcademicSession> sessions = sessionRepository.findAllBySchoolIdOrderByStartDateAsc(schoolId);

        for (AcademicSession session : sessions) {
            if (!today.isBefore(session.getStartDate()) && !today.isAfter(session.getEndDate())) {
                session.setCurrent(true);
                session.setStatus(SessionStatus.ACTIVE);
                sessionRepository.save(session);
                log.info("Activated session '{}' for school {}", session.getName(), schoolId);
                activateFirstTermForSession(school, session);
                return;
            }
        }

        for (AcademicSession session : sessions) {
            if (today.isBefore(session.getStartDate())) {
                session.setCurrent(true);
                session.setStatus(SessionStatus.UPCOMING);
                sessionRepository.save(session);
                log.info("Set upcoming session '{}' as current for school {}", session.getName(), schoolId);
                return;
            }
        }
    }

    private void activateFirstTermForSession(School school, AcademicSession session) {
        Long schoolId = school.getId();
        LocalDate today = LocalDate.now();

        List<Term> terms = termRepository.findAllBySessionIdAndSchoolIdOrderByStartDateAsc(
                session.getId(), schoolId);

        for (Term term : terms) {
            if (!today.isBefore(term.getStartDate()) && !today.isAfter(term.getEndDate())) {
                term.setCurrent(true);
                term.setStatus(TermStatus.ACTIVE);
                termRepository.save(term);
                log.info("Activated term '{}' in new session '{}' for school {}",
                        term.getName(), session.getName(), schoolId);
                return;
            }
        }

        for (Term term : terms) {
            if (today.isBefore(term.getStartDate())) {
                term.setCurrent(true);
                term.setStatus(TermStatus.UPCOMING);
                termRepository.save(term);
                log.info("Set upcoming term '{}' in session '{}' as current for school {}",
                        term.getName(), session.getName(), schoolId);
                return;
            }
        }
    }

    private String deriveNextSessionName(String currentName) {
        try {
            String[] parts = currentName.split("/");
            int startYear = Integer.parseInt(parts[0].trim());
            return (startYear + 1) + "/" + (startYear + 2);
        } catch (Exception e) {
            int currentYear = Year.now().getValue();
            return currentYear + "/" + (currentYear + 1);
        }
    }
}
