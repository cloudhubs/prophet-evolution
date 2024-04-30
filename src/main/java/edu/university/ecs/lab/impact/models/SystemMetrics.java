package edu.university.ecs.lab.impact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
