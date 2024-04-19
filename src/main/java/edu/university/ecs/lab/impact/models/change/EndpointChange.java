package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.Endpoint;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.enums.RestCallImpact;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EndpointChange {

  Endpoint oldEndpoint;
  Endpoint newEndpoint;
  List<Link> oldLinkList;
  List<Link> newLinkList;
  ChangeType changeType;
  // TODO implement impact logic
  RestCallImpact impact;

  public EndpointChange(
      Endpoint oldEndpoint,
      Endpoint newEndpoint,
      List<Link> oldLinkList,
      List<Link> newLinkList,
      ChangeType changeType) {
    this.oldEndpoint = oldEndpoint;
    this.newEndpoint = newEndpoint;
    this.oldLinkList = oldLinkList;
    this.newLinkList = newLinkList;
    this.changeType = changeType;
  }
}
