package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.Endpoint;
import edu.university.ecs.lab.common.models.Method;
import edu.university.ecs.lab.common.models.RestCall;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.enums.EndpointImpact;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class EndpointChange {

  Endpoint oldEndpoint;
  Endpoint newEndpoint;
  Set<Link> oldLinks;
  Set<Link> newLinks;
  ChangeType changeType;
  boolean isChanged;

  EndpointImpact impact;
  List<RestCall> brokenRestCalls;

  private EndpointChange(
      Endpoint oldEndpoint,
      Endpoint newEndpoint,
      Set<Link> oldLinks,
      Set<Link> newLinks,
      ChangeType endpointChangeType) {

    this.oldEndpoint = oldEndpoint;
    this.newEndpoint = newEndpoint;
    this.oldLinks = oldLinks;
    this.newLinks = newLinks;
    this.changeType = endpointChangeType;

    this.isChanged = isChanged();
    this.impact = determineImpact();
  }

  /**
   * Build the changes between two endpoints. This represents all cases (ADD|DELETE|MODIFY) of the
   * endpoint.
   *
   * @param oldEnd original endpoint
   * @param newEnd new endpoint
   * @return object representing change between the two endpoints
   */
  public static EndpointChange buildChange(Endpoint oldEnd, Endpoint newEnd) {
    if (oldEnd == null && newEnd == null) {
      throw new IllegalArgumentException("Both endpoints cannot be null");
    }

    ChangeType changeType =
        oldEnd == null ? ChangeType.ADD : newEnd == null ? ChangeType.DELETE : ChangeType.MODIFY;

    if (oldEnd == null) {
      oldEnd = EndpointChange.blankEndpoint();
    }

    if (newEnd == null) {
      newEnd = EndpointChange.blankEndpoint();
    }

    return new EndpointChange(
        oldEnd, newEnd, Link.fromEndpoint(oldEnd), Link.fromEndpoint(newEnd), changeType);
  }

  public boolean isChanged() {
    if (this.changeType == ChangeType.ADD || this.changeType == ChangeType.DELETE) {
      return true;
    }

    return !(this.oldEndpoint.getParameterList().equals(this.newEndpoint.getParameterList())
        && this.oldEndpoint.getReturnType().equals(this.newEndpoint.getReturnType()));
  }

  private EndpointImpact determineImpact() {
    if (!isChanged) {
      return EndpointImpact.NONE;
    }

    EndpointImpact im = EndpointImpact.fromChangeType(changeType);

    if (changeType != ChangeType.DELETE && newEndpoint.getSrcCalls().isEmpty()) {
      im = EndpointImpact.NOT_USED;
    }

    if (changeType == ChangeType.MODIFY) {
      if (!oldEndpoint.getSrcCalls().isEmpty() && newEndpoint.getSrcCalls().isEmpty()) {
        im = EndpointImpact.NOW_UNUSED;
      }
    }

    return im;
  }

  /**
   * Create a blank endpoint with no information. This represents an endpoint
   * before ADD or after DELETE.
   *
   * @return a blank endpoint
   */
  private static Endpoint blankEndpoint() {
    return new
            Endpoint(new Method("", "", "", new ArrayList<>()), "", "", "", "");
  }
}
