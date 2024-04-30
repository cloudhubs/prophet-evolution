package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.impact.models.change.Link;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SystemMetricsService {
  Map<String, Microservice> oldMicroserviceMap;
  Map<String, Microservice> newMicroserviceMap;
  SystemChange systemChange;

  public SystemMetricsService(
      Map<String, Microservice> oldMicroserviceMap,
      Map<String, Microservice> newMicroserviceMap,
      SystemChange systemChange) {
    this.oldMicroserviceMap = oldMicroserviceMap;
    this.newMicroserviceMap = newMicroserviceMap;
    this.systemChange = systemChange;
  }

  /*
     https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

     Average number of directly connected services (ADCS): the average of ADS metric
     of all services.
  */
  public double calculateADCS(Map<String, Microservice> microserviceMap) {
    MicroserviceMetricsService microserviceMetricsService =
        new MicroserviceMetricsService(oldMicroserviceMap, newMicroserviceMap);
    int totalADS = 0;
    for (Microservice microservice : microserviceMap.values()) {
      totalADS += microserviceMetricsService.calculateADS(microservice);
    }

    return totalADS == 0 ? 0 : ((double) totalADS / microserviceMap.size());
  }

  /*
     https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

     Service Coupling Factor (SCF): measure of the density of a graph’s connectivity.
     SCF = SC/(N2 − N), where SC (service coupling) is a sum of all dependencies
     between services. That is, each service that can invoke operations from another
     service adds one more to this value. N is the total number of services. If we represent
     dependencies as a graph, SC is the sum of all edges and N2 − N represents the
     maximum oriented edges the graph can have.
  */
  public double calculateSCF(Map<String, Microservice> microserviceMap) {
    Set<Link> links = new HashSet<>();

    for (Microservice microservice : microserviceMap.values()) {
      for (JService service : microservice.getServices()) {
        for (RestCall restCall : service.getRestCalls()) {
          links.add(new Link(restCall));
        }
      }
    }

    return (double) links.size()
            / (microserviceMap.values().size() * microserviceMap.values().size())
        - microserviceMap.values().size();
  }
}
