package edu.university.ecs.lab.impact.models.enums;

import lombok.Getter;

@Getter
public enum RestCallImpact {
  NONE("Call is unchanged"),
  CALL_TO_DEPRECATED_ENDPOINT("Call is made to a now deprecated endpoint"),
  CAUSE_UNUSED_ENDPOINT(
      "Change to rest call is now resulting in an unused endpoint (last link(s) were broken)."),
  CHANGED_CALL("Call is changed"),
  HIGH_COUPLING("Call is contributing to high coupling in the system"),
  CYCLE_FORMED("Call is now part of a cyclic dependency in the system"),
  ;

  private final String message;

  RestCallImpact(String message) {
    this.message = message;
  }
}
