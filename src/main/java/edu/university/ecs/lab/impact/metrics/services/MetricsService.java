package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.utils.IRParserUtils;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.impact.models.SystemMetrics;

import java.io.IOException;
import java.util.*;

/**
 * Service to generate metrics from the IR and delta files
 */
public class MetricsService {

  /** Map of microservices name to data in the system */
  private final Map<String, Microservice> oldMicroserviceMap;
  private final Map<String, Microservice> newMicroserviceMap;


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
  public MetricsService(String oldIrPath, String newIrPath, String deltaPath) throws IOException {
    oldMicroserviceMap = IRParserUtils.parseIRSystem(oldIrPath).getServiceMap();
    newMicroserviceMap = IRParserUtils.parseIRSystem(newIrPath).getServiceMap();
    systemChange = IRParserUtils.parseSystemChange(deltaPath);
    classMetricsService = new ClassMetricsService(oldMicroserviceMap, systemChange);
    microserviceMetricsService = new MicroserviceMetricsService(oldMicroserviceMap, newMicroserviceMap);
    systemMetricsService = new SystemMetricsService(oldMicroserviceMap, newMicroserviceMap, systemChange);
  }

  /**
   * Generate overall metrics from the IR/delta files
   *
   * @return system metrics
   */
  public SystemMetrics generateSystemMetrics() {
    SystemMetrics systemMetrics = new SystemMetrics();

    // System metrics first
    systemMetrics.setOldAdcsScore(systemMetricsService.calculateADCS(oldMicroserviceMap));
    systemMetrics.setOldScfScore(systemMetricsService.calculateSCF(newMicroserviceMap));
    systemMetrics.setNewAdcsScore(systemMetricsService.calculateADCS(oldMicroserviceMap));
    systemMetrics.setNewScfScore(systemMetricsService.calculateSCF(newMicroserviceMap));

    // Now class change metrics
    systemMetrics.setClassMetrics(classMetricsService.generateAllClassMetrics());

    // Now Microservice specific metrics
    systemMetrics.setMicroserviceMetrics(microserviceMetricsService.getMicroserviceMetrics());


    return systemMetrics;
  }
}
