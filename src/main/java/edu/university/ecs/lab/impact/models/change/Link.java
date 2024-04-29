package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.RestCall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {
  private String msSource;
  private String msDestination;

  public Link(RestCall restCall) {
    msSource = restCall.getMsId();
    msDestination = restCall.getDestMsId();
  }
}
