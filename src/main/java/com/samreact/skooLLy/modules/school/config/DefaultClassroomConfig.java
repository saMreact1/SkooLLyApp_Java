package com.samreact.skooLLy.modules.school.config;

import com.samreact.skooLLy.modules.school.entity.enums.SchoolType;

import java.util.ArrayList;
import java.util.List;

public class DefaultClassroomConfig {

    public record DefaultClassroom(
            String name,
            String section,
            String level
    ) {}

    public static List<DefaultClassroom> getClassroomsForType(SchoolType type) {
        return switch (type) {
            case NURSERY -> nurseryClasses();
            case PRIMARY -> primaryClasses();
            case SECONDARY -> secondaryClasses();
            case TERTIARY -> new ArrayList<>();
            case NURSERY_AND_PRIMARY -> {
                List<DefaultClassroom> list = new ArrayList<>(nurseryClasses());
                list.addAll(primaryClasses());
                yield list;
            }
            case PRIMARY_AND_SECONDARY -> {
                List<DefaultClassroom> list = new ArrayList<>(primaryClasses());
                list.addAll(secondaryClasses());
                yield list;
            }
            case NURSERY_AND_PRIMARY_AND_SECONDARY -> {
                List<DefaultClassroom> list = new ArrayList<>(nurseryClasses());
                list.addAll(primaryClasses());
                list.addAll(secondaryClasses());
                yield list;
            }
        };
    }

    private static List<DefaultClassroom> nurseryClasses() {
        return List.of(
                new DefaultClassroom("Creche",    "Nursery", "Nursery"),
                new DefaultClassroom("Nursery 1", "Nursery", "Nursery"),
                new DefaultClassroom("Nursery 2", "Nursery", "Nursery"),
                new DefaultClassroom("Nursery 3", "Nursery", "Nursery")
        );
    }

    private static List<DefaultClassroom> primaryClasses() {
        return List.of(
                new DefaultClassroom("Primary 1", "Primary", "Primary"),
                new DefaultClassroom("Primary 2", "Primary", "Primary"),
                new DefaultClassroom("Primary 3", "Primary", "Primary"),
                new DefaultClassroom("Primary 4", "Primary", "Primary"),
                new DefaultClassroom("Primary 5", "Primary", "Primary"),
                new DefaultClassroom("Primary 6", "Primary", "Primary")
        );
    }

    private static List<DefaultClassroom> secondaryClasses() {
        return List.of(
                new DefaultClassroom("JSS 1", "Junior Secondary", "Secondary"),
                new DefaultClassroom("JSS 2", "Junior Secondary", "Secondary"),
                new DefaultClassroom("JSS 3", "Junior Secondary", "Secondary"),
                new DefaultClassroom("SS 1",  "Senior Secondary", "Secondary"),
                new DefaultClassroom("SS 2",  "Senior Secondary", "Secondary"),
                new DefaultClassroom("SS 3",  "Senior Secondary", "Secondary")
        );
    }
}
