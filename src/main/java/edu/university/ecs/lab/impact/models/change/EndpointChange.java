package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.Endpoint;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class EndpointChange {
  private static final String ACTION_MSG1 = "A Endpoint was Deleted!";
  private static final String ACTION_MSG2 = "A Endpoint was Added!";
  private static final String ACTION_MSG3 = "A Endpoint was Modified!";

  private static final String IMPACT_MSG1 = "Old Links were Broken!";
  private static final String IMPACT_MSG2 = "New Link were Created!";

  Endpoint oldEndpoint;
  Endpoint newEndpoint;
  List<Link> oldLinkList;
  List<Link> newLinkList;
  String action;
  String impact;

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

    setChangeType(changeType);
    //        setImpact();
  }

  private void setChangeType(ChangeType changeType) {
    switch (changeType) {
      case DELETE:
        action = ACTION_MSG1;
        break;
      case ADD:
        action = ACTION_MSG2;
        break;
      case MODIFY:
        action = ACTION_MSG3;
        break;
    }
  }

  //    private void setImpact() {
  //        if(Objects.nonNull(newLink) && newLink.getMsDestination().equals("?")) {
  //            impact = IMPACT_MSG2;
  //        }
  //    }

}
