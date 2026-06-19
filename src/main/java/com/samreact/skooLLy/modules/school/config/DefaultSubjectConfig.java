package com.samreact.skooLLy.modules.school.config;

import com.samreact.skooLLy.modules.school.entity.enums.SchoolType;

import java.util.ArrayList;
import java.util.List;

public class DefaultSubjectConfig {

    public record DefaultSubject(
            String name,
            String code,
            String category,
            boolean isElective
    ) {}

    public static List<DefaultSubject> getSubjectsForType(SchoolType type) {
        return switch (type) {
            case NURSERY -> nurserySubjects();
            case PRIMARY -> primarySubjects();
            case SECONDARY -> secondarySubjects();
            case TERTIARY -> new ArrayList<>();
            case NURSERY_AND_PRIMARY -> {
                List<DefaultSubject> list = new ArrayList<>(nurserySubjects());
                list.addAll(primarySubjects());
                yield list;
            }
            case PRIMARY_AND_SECONDARY -> {
                List<DefaultSubject> list = new ArrayList<>(primarySubjects());
                list.addAll(secondarySubjects());
                yield list;
            }
            case NURSERY_AND_PRIMARY_AND_SECONDARY -> {
                List<DefaultSubject> list = new ArrayList<>(nurserySubjects());
                list.addAll(primarySubjects());
                list.addAll(secondarySubjects());
                yield list;
            }
        };
    }

    private static List<DefaultSubject> nurserySubjects() {
        return List.of(
                new DefaultSubject("Phonics", "PHO", "Language Arts", false),
                new DefaultSubject("Numeracy", "NUM", "Mathematics", false),
                new DefaultSubject("Creative Arts", "CRA", "Arts", false),
                new DefaultSubject("Social Habits", "SOH", "Social Development", false),
                new DefaultSubject("French", "FRE", "Languages", false),
                new DefaultSubject("Physical & Health Education", "PHE", "Physical Development", false),
                new DefaultSubject("Agricultural Science", "AGS", "Science", false),
                new DefaultSubject("Civic Education", "CVE", "Social Studies", false)
        );
    }

    private static List<DefaultSubject> primarySubjects() {
        return List.of(
                new DefaultSubject("English Language", "ENG", "Language Arts", false),
                new DefaultSubject("Mathematics", "MTH", "Mathematics", false),
                new DefaultSubject("Basic Science and Technology", "BST", "Science", false),
                new DefaultSubject("Social Studies", "SOS", "Social Studies", false),
                new DefaultSubject("Civic Education", "CVE", "Social Studies", false),
                new DefaultSubject("French", "FRE", "Languages", false),
                new DefaultSubject("Creative Arts", "CRA", "Arts", false),
                new DefaultSubject("Physical & Health Education", "PHE", "Physical Development", false),
                new DefaultSubject("Agricultural Science", "AGS", "Science", false),
                new DefaultSubject("Christian Religious Studies", "CRS", "Religious Studies", true),
                new DefaultSubject("Islamic Religious Studies", "IRS", "Religious Studies", true)
        );
    }

    private static List<DefaultSubject> secondarySubjects() {
        return List.of(
                new DefaultSubject("English Language", "ENG", "Language Arts", false),
                new DefaultSubject("Mathematics", "MTH", "Mathematics", false),
                new DefaultSubject("Physics", "PHY", "Science", false),
                new DefaultSubject("Chemistry", "CHM", "Science", false),
                new DefaultSubject("Biology", "BIO", "Science", false),
                new DefaultSubject("Economics", "ECO", "Commercial", false),
                new DefaultSubject("Government", "GOV", "Humanities", false),
                new DefaultSubject("Literature in English", "LIT", "Humanities", false),
                new DefaultSubject("French", "FRE", "Languages", false),
                new DefaultSubject("Computer Science", "CMP", "Science & Technology", false),
                new DefaultSubject("Agricultural Science", "AGS", "Science", false),
                new DefaultSubject("Civic Education", "CVE", "Social Studies", false),
                new DefaultSubject("Technical Drawing", "TDR", "Science & Technology", true),
                new DefaultSubject("Further Mathematics", "FMT", "Mathematics", true),
                new DefaultSubject("Christian Religious Studies", "CRS", "Humanities", true),
                new DefaultSubject("Islamic Religious Studies", "IRS", "Humanities", true)
        );
    }
}
