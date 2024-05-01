package edu.university.ecs.lab.impact.models.enums;

import lombok.Getter;

@Getter
public enum Impact {
    NONE("No risk"),
    QUALITY("Quality Detriment"),
    WILL_FAULT("Will cause fault");

    private final String name;

    Impact(String name) {
        this.name = name;
    }
}
