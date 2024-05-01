package edu.university.ecs.lab.impact.models.enums;

import edu.university.ecs.lab.delta.models.enums.ChangeType;
import lombok.Getter;

@Getter
public enum EndpointImpact {
  NONE("No change", "Endpoint is unchanged", Impact.NONE, ""),
  NOW_UNUSED("Endpoint now unused", "Endpoint is not invoked by rest calls as of this commit.", Impact.QUALITY, "Endpoint should be investigated for removal for maintainability."),
  NOT_USED("Endpoint not used", "Endpoint is not invoked by rest calls.", Impact.QUALITY, "Endpoint should be investigated for removal for maintainability."),
  ADD("Added endpoint", "New endpoint", Impact.NONE, ""),
  DELETE("Deleted Endpoint", "Deleted endpoint", Impact.NONE, ""),
  IS_CHANGED("Modified endpoint","Endpoint is changed", Impact.NONE, ""),
  BROKE_DEPENDENT_CALLS("Broken dependent calls",
      "Endpoint is changed, resulting in broken rest calls that invoked it and were not updated"
          + " correctly. This will cause these methods to fault.", Impact.WILL_FAULT, "All rest calls directed at this endpoint will fault."),
  ;

  private final String name;
  private final String message;
  private final Impact impact;
  private final String impactMsg;

  EndpointImpact(String name, String message, Impact impact, String impactMsg) {
    this.name = name;
    this.message = message;
    this.impact = impact;
    this.impactMsg = impactMsg;
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
