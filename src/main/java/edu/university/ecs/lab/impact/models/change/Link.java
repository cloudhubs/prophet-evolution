package edu.university.ecs.lab.impact.models.change;

import edu.university.ecs.lab.common.models.Endpoint;
import edu.university.ecs.lab.common.models.RestCall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

  private Link(Endpoint endpoint, RestCall.EndpointCall call) {
    msSource = call.getMsId();
    msDestination = endpoint.getMsId();
  }

  public static Set<Link> fromEndpoint(Endpoint endpoint) {
    if (endpoint == null) {
      return new HashSet<>();
    }

    return endpoint.getSrcCalls().stream()
        .map(call -> new Link(endpoint, call))
        .collect(Collectors.toSet());
  }
}
