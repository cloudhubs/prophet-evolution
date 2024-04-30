package edu.university.ecs.lab.impact.models.enums;

import edu.university.ecs.lab.delta.models.enums.ChangeType;
import lombok.Getter;

@Getter
public enum EndpointImpact {
    NONE("Endpoint is unchanged"),
    NOW_UNUSED("Endpoint is not invoked by rest calls as of this commit."),
    NOT_USED("Endpoint is not invoked by rest calls."),
    ADD("New endpoint"),
    DELETE("Deleted endpoint"),
    IS_CHANGED("Endpoint is changed"),
    BROKE_DEPENDENT_CALLS("Endpoint is changed, resulting in broken rest calls that invoked it and were not updated correctly. " +
            "This will cause these methods to fault."),
    ;

    private final String message;

    EndpointImpact(String message) {
        this.message = message;
    }

    public static EndpointImpact fromChangeType(ChangeType changeType) {
        switch (changeType) {
            case ADD:
                return ADD;
            case DELETE:
                return DELETE;
            case MODIFY:
                return IS_CHANGED;
            default:
                return NONE;
        }
    }
}
