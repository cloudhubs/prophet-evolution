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
    String[] sourceFileParts = restCall.getSourceFile().split("/");
    String[] destFileParts = restCall.getDestFile().split("/");

    msSource = sourceFileParts[1];
    msDestination = restCall.getDestFile().isEmpty() ? "?" : destFileParts[1];
  }
}
