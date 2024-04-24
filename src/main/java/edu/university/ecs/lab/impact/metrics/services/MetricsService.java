package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.IRParserUtils;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.impact.models.SystemMetrics;
import edu.university.ecs.lab.impact.models.change.EndpointChange;
import edu.university.ecs.lab.impact.models.change.Metric;

import java.io.IOException;
import java.util.*;

/**
 * Service to generate metrics from the IR and delta files
 */
public class MetricsService {

  /** Map of microservices name to data in the system */
  private final Map<String, Microservice> microserviceMap;

  /** Object representing changes to the system as a whole/overall changes */
  private final SystemChange systemChange;
  private final ClassMetricsService classMetricsService;
  private final MicroserviceMetricsService microserviceMetricsService;
  private final SystemMetricsService systemMetricsService;

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
    classMetricsService = new ClassMetricsService(microserviceMap, systemChange);
    microserviceMetricsService = new MicroserviceMetricsService(microserviceMap, systemChange);
    systemMetricsService = new SystemMetricsService(microserviceMap, systemChange);
  }

  /**
   * Generate overall metrics from the IR/delta files
   *
   * @return system metrics
   */
  public SystemMetrics generateSystemMetrics() {
    SystemMetrics systemMetrics = new SystemMetrics();

    // System metrics first
    systemMetrics.setAdcsScore(systemMetricsService.calculateADCS());
    systemMetrics.setScfScore(systemMetricsService.calculateSCF());

    // Now class change metrics
    systemMetrics.setClassMetrics(classMetricsService.generateAllClassMetrics());

    // Now Microservice specific metrics
    systemMetrics.setMicroserviceMetrics(microserviceMetricsService.getMicroserviceMetrics());



    return systemMetrics;
  }

//  @Deprecated
//  private List<Metric> getMetrics() {
//    List<Metric> metricList = new ArrayList<>();
//    Metric metric;
//
//    // Handle all service changes
//    if (Objects.nonNull(systemChange.getServices()) && !systemChange.getServices().isEmpty()) {
//      for (Delta delta : systemChange.getServices()) {
//        metric = new Metric();
//        metric.setFilePath(delta.getLocalPath());
//        metric.setCallChangeList(callChangeService.getRestCallChangesForDelta(delta));
//        metric.setClassRole(ClassRole.SERVICE);
//
//        metric.setChangeType(delta.getChangeType());
//        metric.setMicroserviceName(delta.getMsName());
//        metricList.add(metric);
//
//      }
//    }
//
//    // Handle all controller changes
//    if (Objects.nonNull(systemChange.getControllers())
//        && !systemChange.getControllers().isEmpty()) {
//      for (Delta delta : systemChange.getControllers()) {
//        metric = new Metric();
//        metric.setFilePath(delta.getLocalPath());
//        metric.setEndpointChangeList(endpointChangeService.getEndpointChangesForDelta(delta));
//        metric.setClassRole(ClassRole.CONTROLLER);
//
//        metric.setChangeType(delta.getChangeType());
//        metric.setMicroserviceName(delta.getMsName());
//        metricList.add(metric);
//      }
//    }
//
//    return metricList;
//  }
}
