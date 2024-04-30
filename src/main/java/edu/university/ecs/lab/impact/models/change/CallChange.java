package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.Microservice;
import edu.university.ecs.lab.common.models.RestCall;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.enums.RestCallImpact;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

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

  public CallChange(RestCall oldCall, RestCall newCall, ChangeType changeType) {
    this.oldCall = oldCall;
    this.newCall = newCall;
    this.changeType = changeType;
    this.oldLink = Objects.isNull(oldCall) ? null : new Link(oldCall);
    this.newLink = Objects.isNull(newCall) ? null : new Link(newCall);
    this.impact = RestCallImpact.NONE;

  }
}
