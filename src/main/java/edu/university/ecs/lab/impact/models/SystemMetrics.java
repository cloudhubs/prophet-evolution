package edu.university.ecs.lab.impact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetrics {

  private int brokenApiDependencies;
  private int newApiDependencies;
  private int affectedEntityDependencies;
  private int newEntityDependencies;
  private int newSystems;
  private int modifiedClasses;

  private List<ClassMetrics> classMetrics;
  private DependencyMetrics dependencyMetrics;

  // private double systemCoupling;

  /**
   * Should include all simple (not map, list, large object, etc.) metrics to render at the beginning of the class metrics report
   * @return a map of the simple metrics
   */
  public Map<String, Object> getOverallMetricsAsMap() {
    return Map.of(
        "Broken API Dependencies", brokenApiDependencies,
        "New API Dependencies", newApiDependencies,
        "Affected Entity Dependencies", affectedEntityDependencies,
        "New Entity Dependencies", newEntityDependencies,
        "New Systems", newSystems,
        "Modified Classes", modifiedClasses
    );
  }

}
