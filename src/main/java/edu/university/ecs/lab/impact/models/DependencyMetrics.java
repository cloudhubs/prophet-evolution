package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.change.EndpointChange;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependencyMetrics {
  List<CallChange> callChanges;
  List<EndpointChange> endpointChanges;
}
