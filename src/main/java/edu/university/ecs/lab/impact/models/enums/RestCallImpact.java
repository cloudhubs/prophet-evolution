package edu.university.ecs.lab.impact.models.enums;

public enum RestCallImpact {
    NONE,
    CALL_TO_DEPRECATED_ENDPOINT,
    CAUSE_UNUSED_ENDPOINT,
    INCONSISTENT_CALL,
    HIGH_COUPLING,
    CYCLE_FORMED
    ;

    private final String message;

    RestCallImpact(String message) {
        this.message = message;
    }
}
