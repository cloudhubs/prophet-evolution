package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.Endpoint;
import edu.university.ecs.lab.common.models.RestCall;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.enums.EndpointImpact;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EndpointChange {

  Endpoint oldEndpoint;
  Endpoint newEndpoint;
  List<Link> oldLinkList;
  List<Link> newLinkList;
  ChangeType changeType;

  EndpointImpact impact;
  List<RestCall> brokenRestCalls;


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
    this.impact = EndpointImpact.NONE;
  }

  /**
   * Build the changes between two controllers.
   *
   * @param oldController
   * @param newController
   * @return
   */
  public static EndpointChange buildChange(Endpoint oldEnd, Endpoint newEnd) {

    return null;
  }

  public static EndpointChange addOrDeleteControllerChange(Endpoint endpoint, List<Link> linkList, ChangeType changeType) {
    switch (changeType) {
      case ADD:
        return new EndpointChange(null, endpoint, new ArrayList<>(), linkList, changeType);
      case DELETE:
        return new EndpointChange(endpoint, null, linkList, new ArrayList<>(), changeType);
      default:
        return null;
    }
  }

  public static boolean isChanged(Endpoint oldEndpoint, Endpoint newEndpoint) {

  }
}
