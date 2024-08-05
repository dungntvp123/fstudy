package com.project.fstudy.data.constant;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum TaskStatus {
    TO_DO("To Do"),
    ON_GOING("On Going"),
    FINISH("Finish"),
    PENDING("Pending");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static TaskStatus fromDisplayName(String displayName) {
        return Arrays.stream(TaskStatus.values())
                .filter(status -> status.getDisplayName().toLowerCase().equals(displayName.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
