package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.impact.metrics.services.MicroserviceMetricsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetrics {


  // System Metrics
  private double scfScore;
  private double adcsScore;

  private List<ClassMetrics> classMetrics;
  private List<MicroserviceMetrics> microserviceMetrics;

  // private double systemCoupling;

  /**
   * Should include all simple (not map, list, large object, etc.) metrics to render at the
   * beginning of the class metrics report
   *
   * @return a map of the simple metrics
   */
//  public Map<String, Object> getOverallMetricsAsMap() {
//    return Map.of(
//        "Broken API Dependencies", brokenApiDependencies,
//        "New API Dependencies", newApiDependencies,
//        "Affected Entity Dependencies", affectedEntityDependencies,
//        "New Entity Dependencies", newEntityDependencies,
//        "New Systems", newSystems,
//        "Modified Classes", modifiedClasses);
//  }
}
