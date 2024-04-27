package edu.university.ecs.lab.impact.models.enums;

public enum RestCallImpact {
  NONE,
  CALL_TO_DEPRECATED_ENDPOINT,
  UNUSED_ENDPOINT,
  INCONSISTENT_CALL,
  HIGH_COUPLING,
  CYCLE_FORMED
}
