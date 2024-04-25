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
  private double oldScfScore;
  private double oldAdcsScore;
  private double newScfScore;
  private double newAdcsScore;

  private List<ClassMetrics> classMetrics;
  private List<MicroserviceMetrics> microserviceMetrics;
}
