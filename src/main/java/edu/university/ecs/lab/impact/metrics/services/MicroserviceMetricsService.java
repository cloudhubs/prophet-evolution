package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.impact.models.DependencyMetrics;
import edu.university.ecs.lab.impact.models.MicroserviceMetrics;
import edu.university.ecs.lab.impact.models.change.Link;

import java.util.*;

public class MicroserviceMetricsService {
  public static final int THRESHOLD = 5;

  private final Map<String, Microservice> oldMicroserviceMap;
  private final Map<String, Microservice> newMicroserviceMap;

  private final CallChangeService callChangeService;
  private final EndpointChangeService endpointChangeService;

  public MicroserviceMetricsService(
      Map<String, Microservice> oldMicroserviceMap, Map<String, Microservice> newMicroserviceMap) {
    this.oldMicroserviceMap = oldMicroserviceMap;
    this.newMicroserviceMap = newMicroserviceMap;

    this.callChangeService = new CallChangeService(oldMicroserviceMap, newMicroserviceMap);
    this.endpointChangeService = new EndpointChangeService(oldMicroserviceMap, newMicroserviceMap);
  }

  public List<MicroserviceMetrics> getMicroserviceMetrics() {
    List<MicroserviceMetrics> microserviceMetricsList = new ArrayList<>();

    // Go through the old map and determine changed services
    for (Microservice oldMicroservice : oldMicroserviceMap.values()) {
      Microservice newMicroservice = newMicroserviceMap.get(oldMicroservice.getId());
      microserviceMetricsList.add(buildMetrics(oldMicroservice, newMicroservice));
    }

    // Handle new services
    for (Microservice newMicroservice : newMicroserviceMap.values()) {
      if (oldMicroserviceMap.get(newMicroservice.getId()) == null) {
        microserviceMetricsList.add(buildMetrics(null, newMicroservice));
      }
    }

    return microserviceMetricsList;
  }

  private MicroserviceMetrics buildMetrics(
      Microservice oldMicroservice, Microservice newMicroservice) {
    MicroserviceMetrics microserviceMetrics;
    microserviceMetrics = new MicroserviceMetrics(oldMicroservice, newMicroservice);

    microserviceMetrics.generateSiucMetrics(oldMicroserviceMap, newMicroserviceMap);

    microserviceMetrics.setDependencyMetrics(
        new DependencyMetrics(
            callChangeService.getMsRestCallChanges(oldMicroservice, newMicroservice),
            endpointChangeService.getAllMsEndpointChanges(oldMicroservice, newMicroservice)));
    return microserviceMetrics;
  }

  /*
     https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

         Service Interface Data Cohesion (SIDC1): this metric quantifies
     the cohesion of a service based on the cohesiveness
     of the operations exposed in its interface, which means
     operations sharing the same type of input parameter [16].
     SIDC2 evolves from SIDC1 by considering both in/out parameter types.

     Formal definition:

     SIDC(s) = (Common(Param(SOp(sis )))
     + Common(returnType(SOp(sis ))))/
     (Total(SOp(sis )) ∗ 2), where
     – SOp(sis ) is the set of all operations exposed in the interface
     sis of service s.
     – (Common(Param(SOp(sis )))returns the number of service
     operations pairs which have at least one input parameter
     type in common.
     – Common(returnType(SOp(sis )))) returns the number of
     service operations pairs which have a same return type.
     – Total(SOp(sis )) returns the number of combinations of
     operation pairs for the service interface sis .
  */
  public static double calculateSIDC2Score(Microservice microservice) {
    Map<String, Integer> paramTypes = new HashMap<>();
    Map<String, Integer> returnTypes = new HashMap<>();
    int commonParams = 0;
    int commonReturns = 0;
    int totalEndpoints = 0;

    for (JController controller : microservice.getControllers()) {
      {
        for (Endpoint endpoint : controller.getEndpoints()) {
          totalEndpoints++;
          String[] params = endpoint.getParameterList().split(",");
          for (String param : params) {
            String[] paramParts = param.split(" ");
            if (paramParts.length == 2) {
              // If we try to add it but it was already there
              if (paramTypes.containsKey(paramParts[0])) {
                paramTypes.merge(paramParts[0], 1, Integer::sum);
                commonParams += (paramTypes.get(paramParts[0]) - 1) * 2;
              } else {
                paramTypes.put(paramParts[0], 1);
              }
            }
          }

          if (returnTypes.containsKey(endpoint.getReturnType())) {
            returnTypes.merge(endpoint.getReturnType(), 1, Integer::sum);
            commonReturns += (returnTypes.get(endpoint.getReturnType()) - 1) * 2;
          } else {
            returnTypes.put(endpoint.getReturnType(), 1);
          }
        }
      }
    }

    return totalEndpoints == 0 ? 0 : (double) (commonParams + commonReturns) / (totalEndpoints * 2);
  }

  /*
     https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

     Service Interface Usage Cohesion (SIUC): this metric
     quantifies the client usage patterns of service operations
     when a client invokes a service.

     Formal definition:
     SIUC(s) = Invoked(clients, SOp(sis ))/
     (|clients| ∗ |SOp(sis )|), where
     – clients is the set of all clients of service s.
     – Invoked(clients, SOp(sis )) returns the number of used
     operations per client.
  */
  public static double calculateSIUCScore(
      Map<String, Microservice> microserviceMap, Microservice microservice) {
    Map<String, Integer> clients = new HashMap<>();

    int usedOperations = 0;

    for (JController controller : microservice.getControllers()) {

      // Loop through endpoints
      for (Endpoint endpoint : controller.getEndpoints()) {

        // Look for links from "clients"
        for (Microservice ms : microserviceMap.values()) {
          for (JService service : ms.getServices()) {
            for (RestCall restCall : service.getRestCalls()) {
              if (restCall.getDestEndpoint().equals(endpoint.getUrl())) {
                usedOperations++;
                clients.putIfAbsent(ms.getId(), 0);
                clients.merge(ms.getId(), 1, Integer::sum);
              }
            }
          }
        }
      }
    }

    if (usedOperations == 0) {
      return 0;
    }

    double siuc = 0;

    for (int i : clients.values()) {
      siuc += i;
    }

    siuc /= clients.size();

    siuc /= (clients.size() * siuc);

    return siuc;
  }

  /*
     https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

     Absolute Dependence of the Service (ADS): number of services on which the S1
     service depends. In other words, ADS is the number of services that S1 calls for its
     operation to be complete. The higher the ADS, the more this service depends on other
     services, i.e., it is more vulnerable to the side effects of failures in the services invoked.

     In other words this is the coupling degree with other services
  */
  public static int calculateADS(Microservice microservice) {
    Set<Link> links = new HashSet<>();
    for (JService service : microservice.getServices()) {
      for (RestCall restCall : service.getRestCalls()) {
        links.add(new Link(restCall));
      }
    }

    return links.size();
  }
}
