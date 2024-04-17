package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.IRParserUtils;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.impact.models.SystemMetrics;
import edu.university.ecs.lab.impact.models.change.Metric;

import java.io.IOException;
import java.util.*;

/**
 * Service to generate metrics from the IR and delta files File writing logic should be placed in
 * {@link MetricFileWriterService} to separate concerns
 */
public class MetricsService {

  /** Map of microservices name to data in the system */
  private Map<String, Microservice> microserviceMap;

  /** Object representing changes to the system as a whole/overall changes */
  private SystemChange systemChange;

  private final DependencyMetricsService dependencyMetricsService;
  private final ClassMetricsService classMetricsService;

  private final MetricFileWriterService fileWriterService;

  /**
   * Constructor for MetricsService
   *
   * @param oldIrPath path to the original system IR
   * @param deltaPath path to the system delta
   * @throws IOException if system metrics cannot be generated
   */
  public MetricsService(String oldIrPath, String deltaPath) throws IOException {
    microserviceMap = IRParserUtils.parseIRSystem(oldIrPath).getServiceMap();
    systemChange = IRParserUtils.parseSystemChange(deltaPath);
    dependencyMetricsService = new DependencyMetricsService(microserviceMap);
    classMetricsService = new ClassMetricsService();
    fileWriterService = new MetricFileWriterService(this);
  }

  /**
   * Wrapper method to write metrics to file
   * @param fileName name of file to write to
   * @throws IOException if file cannot be written to, generated in {@link MetricFileWriterService#writeMetricsToFile(String, SystemMetrics)
   */
  public void writeMetricsToFile(String fileName) throws IOException {
    fileWriterService.writeMetricsToFile(fileName, this.generateSystemMetrics());
  }

  /**
   * Generate overall metrics from the IR/delta files
   *
   * @return system metrics
   */
  public SystemMetrics generateSystemMetrics() {
    SystemMetrics systemMetrics = new SystemMetrics();

    systemMetrics.setClassMetrics(classMetricsService.generateAllClassMetrics(systemChange));
    //    systemMetrics.setDependencyMetrics(
    //        dependencyMetricsService.generateAllDependencyMetrics(systemChange));

    // TODO Handle the other attributes of system metrics

    return systemMetrics;
  }

  public List<Metric> getPlaceholders() {
    List<Metric> metricList = new ArrayList<>();
    Metric metric;

    // Handle all service changes
    if (Objects.nonNull(systemChange.getServices()) && !systemChange.getServices().isEmpty()) {
      for (Delta delta : systemChange.getServices()) {
        metric = new Metric();
        metric.setFilePath(delta.getLocalPath());
        metric.setCallChangeList(dependencyMetricsService.getRestCallChangesForDelta(delta));
        metric.setClassRole(ClassRole.SERVICE);

        metric.setChangeType(delta.getChangeType());
        metric.setMicroserviceName(delta.getMsName());
        metricList.add(metric);
        // TODO RestCall Changes

      }
    }

    // Handle all controller changes
    if (Objects.nonNull(systemChange.getControllers())
        && !systemChange.getControllers().isEmpty()) {
      for (Delta delta : systemChange.getControllers()) {
        metric = new Metric();
        metric.setFilePath(delta.getLocalPath());
        metric.setEndpointChangeList(dependencyMetricsService.getEndpointChangesForDelta(delta));
        metric.setClassRole(ClassRole.CONTROLLER);

        metric.setChangeType(delta.getChangeType());
        metric.setMicroserviceName(delta.getMsName());
        metricList.add(metric);
        // TODO RestCall Changes
      }
    }

    return metricList;
  }
}
