package edu.university.ecs.lab.impact.models.enums;

import lombok.Getter;

@Getter
public enum RestCallImpact {
  NONE("No change", "Call is unchanged", Impact.NONE, ""),
  CALL_TO_DEPRECATED_ENDPOINT("Call to Deprecated Endpoint", "Call is made to an endpoint that no longer exists", Impact.WILL_FAULT,
          "Call will fault as the call destination is unreachable"),
  CAUSE_UNUSED_ENDPOINT("Caused Unused Endpoint",
      "Change to rest call is now resulting in an unused endpoint (last link(s) were broken).", Impact.QUALITY,
          "Endpoint should be investigated for removal for maintainability."),
  CHANGED_CALL("Modified Call", "Call is changed", Impact.NONE, ""),
  HIGH_COUPLING("High Coupling", "Call is contributing to high coupling in the system", Impact.QUALITY,
          "High coupling in the system is a known anti-pattern and should be avoided, as accumulation of technical debt is likely " +
                  "leading to maintainability degradation"),
  CYCLE_FORMED("Cyclic Dependency","Call is now part of a cyclic dependency in the system", Impact.QUALITY,
          "Cyclic dependencies are known to cause many issues in microservice systems, including " +
                  "decreased maintainability, decreased reliability, and a higher likelihood of failure as faults propagate through the cycle"),
  ;

  private final String name;
  private final String message;
  private final Impact impact;
  private final String impactMsg;

  RestCallImpact(String name, String message, Impact impact, String impactMsg) {
    this.name = name;
    this.message = message;
    this.impact = impact;
    this.impactMsg = impactMsg;
  }
}
