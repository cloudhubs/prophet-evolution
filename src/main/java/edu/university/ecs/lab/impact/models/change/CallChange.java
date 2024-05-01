package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.RestCall;
import edu.university.ecs.lab.common.models.enums.HttpMethod;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.enums.RestCallImpact;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Change to the call itself. A change type of 'Add' means that a new call was created. A change
 * type of 'Delete' means that a call was deleted. A change type of 'Modify' means that a call was
 * modified to point somewhere else.
 */
@Data
@NoArgsConstructor
public class CallChange {
  RestCall oldCall;
  RestCall newCall;
  Link oldLink;
  Link newLink;
  ChangeType changeType;
  RestCallImpact impact;

  private CallChange(RestCall oldCall, RestCall newCall, ChangeType changeType) {
    this.oldCall = oldCall;
    this.newCall = newCall;
    this.changeType = changeType;
    this.oldLink = new Link(oldCall);
    this.newLink = new Link(newCall);
    this.impact = RestCallImpact.NONE;
  }

  /**
   * Build the changes between two endpoints. This represents all cases (ADD|DELETE|MODIFY) of the
   * endpoint.
   *
   * @param oldCall original endpoint
   * @param newCall new endpoint
   * @return object representing change between the two endpoints
   */
  public static CallChange buildChange(RestCall oldCall, RestCall newCall) {
    if (oldCall == null && newCall == null) {
      throw new IllegalArgumentException("Both calls cannot be null");
    }

    // TODO remove modify and verify one is null
    ChangeType changeType =
        oldCall == null ? ChangeType.ADD : newCall == null ? ChangeType.DELETE : ChangeType.MODIFY;

    if (oldCall == null) {
      oldCall = CallChange.blankCall();
    }

    if (newCall == null) {
      newCall = CallChange.blankCall();
    }

    return new CallChange(oldCall, newCall, changeType);
  }

  /**
   * Create a blank endpoint with no information. This represents an endpoint before ADD or after
   * DELETE.
   *
   * @return a blank endpoint
   */
  private static RestCall blankCall() {
    return new RestCall("", "", "", "", HttpMethod.NONE, "", "", "", "");
  }
}
