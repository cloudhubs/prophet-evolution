package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.common.models.Microservice;
import edu.university.ecs.lab.impact.metrics.services.MicroserviceMetricsService;
import edu.university.ecs.lab.impact.models.change.Link;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class SystemMetrics {
  // System Metrics
  private double oldScfScore;
  private double oldAdcsScore;
  private double newScfScore;
  private double newAdcsScore;

  private List<ClassMetrics> classMetrics;
  private List<MicroserviceMetrics> microserviceMetrics;

  private SystemMetrics() {
    super();
  }

  public static SystemMetrics buildSystemMetrics(
      Map<String, Microservice> oldMicroserviceMap,
      Map<String, Microservice> newMicroserviceMap,
      List<ClassMetrics> classMetrics,
      List<MicroserviceMetrics> microserviceMetrics) {
    SystemMetrics metrics = new SystemMetrics();
    metrics.oldScfScore = calculateSCF(oldMicroserviceMap);
    metrics.oldAdcsScore = calculateADCS(oldMicroserviceMap);
    metrics.newScfScore = calculateSCF(newMicroserviceMap);
    metrics.newAdcsScore = calculateADCS(newMicroserviceMap);
    metrics.classMetrics = classMetrics;
    metrics.microserviceMetrics = microserviceMetrics;

    // Sort system metrics by number of changed endpoints and calls
    metrics.sortMicroserviceMetrics();

    return metrics;
  }

  /*
     https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

     Average number of directly connected services (ADCS): the average of ADS metric
     of all services = sumADS / numServices.

     Numbers closer to 0 indicate a graph with lower coupling
  */
  private static double calculateADCS(Map<String, Microservice> microserviceMap) {
    int totalADS = 0;

    for (Microservice microservice : microserviceMap.values()) {
      totalADS += MicroserviceMetricsService.calculateADS(microservice);
    }

    double numServices = microserviceMap.size();
    return numServices == 0 ? 0 : ((double) totalADS / numServices);
  }

  /*
     https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

     Service Coupling Factor (SCF): measure of the density of a graph’s connectivity.
     SCF = SC/(N * (N+1)), where SC (service coupling) is a sum of all dependencies
     between services. That is, each service that can invoke operations from another
     service adds one more to this value. N is the total number of services. If we represent
     dependencies as a graph, SC is the sum of all edges and N(N+1) represents the
     maximum oriented edges the graph can have.

     Numbers closer to 0 indicate a less dense graph and hence a loosely coupled system
  */
  private static double calculateSCF(Map<String, Microservice> microserviceMap) {
    Set<Link> links = new HashSet<>();

    // For every call, create a link
    for (Microservice microservice : microserviceMap.values()) {
      links.addAll(microservice.getAllLinks());
    }
    links = links.stream().filter(Link::hasDestination).collect(Collectors.toSet());

    double serviceCount = microserviceMap.values().size();
    double maxConnectivity = serviceCount * (serviceCount + 1);

    return maxConnectivity == 0 ? 0 : (double) links.size() / maxConnectivity;
  }

  private void sortMicroserviceMetrics() {
    this.microserviceMetrics.sort(
        Comparator.comparing(
            m ->
                -1
                    * (m.getDependencyMetrics().getCallChanges().size()
                        + m.getDependencyMetrics().getEndpointChanges().size())));
  }
}
