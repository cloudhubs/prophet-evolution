package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.utils.IRParserUtils;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.DependencyMetrics;
import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.change.EndpointChange;
import edu.university.ecs.lab.impact.models.change.Link;
import edu.university.ecs.lab.impact.models.dependency.ApiDependency;
import edu.university.ecs.lab.impact.models.dependency.EntityDependency;
import edu.university.ecs.lab.impact.models.enums.RestCallImpact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static edu.university.ecs.lab.common.utils.FlowUtils.buildFlows;

public class DependencyMetricsService {
  Map<String, Microservice> microserviceMap;
  SystemChange systemChange;

  public DependencyMetricsService(Map<String, Microservice> microserviceMap, SystemChange systemChange) {
    this.microserviceMap = microserviceMap;
    this.systemChange = systemChange;
  }

  public DependencyMetrics generateAllDependencyMetrics(SystemChange systemChange) {
    DependencyMetrics metrics = new DependencyMetrics();
    // TODO
    // metrics.setEntityDependencyList(getAllEntityDeps());
    return metrics;
  }

  private List<ApiDependency> getServiceAPIDeps(Microservice microservice) {
    List<ApiDependency> apiDependencies = new ArrayList<>();

    for (JService jService : microservice.getServices()) {
      for (RestCall restCall : jService.getRestCalls()) {
        if (!restCall.getDestFile().isEmpty()) {
          apiDependencies.add(
              new ApiDependency(
                  null,
                  IRParserUtils.getClassNameFromLocalPath(restCall.getSourceFile()),
                  IRParserUtils.getClassNameFromLocalPath(restCall.getDestFile()),
                  IRParserUtils.getClassNameFromLocalPath(restCall.getSourceFile()),
                  IRParserUtils.getClassNameFromLocalPath(restCall.getDestFile()),
                  true));
        }
      }
    }

    return apiDependencies;
  }

  private List<EntityDependency> getServiceEntityDeps(Microservice microservice) {
    List<EntityDependency> entityDependencyList = new ArrayList<>();
    EntityDependency entityDependency;

    //        // If no entities or dto's are modified then entity dependencies are not affected
    //        if(checkForEntityModification(systemChange)) {
    //            return;
    //        }

    List<Flow> flows = buildFlows(microservice);

    for (JClass entity : microservice.getEntities()) {
      for (Flow flow : flows) {
        if (flow.getControllerMethod().getParameterList().contains(entity.getClassName())) {
          entityDependency =
              new EntityDependency(
                  null,
                  microservice.getId(),
                  entity.getClassName(),
                  flow.getController().getClassName());
          entityDependencyList.add(entityDependency);
        }
      }
    }

    return entityDependencyList;
  }

  /**
   * Checks if relevant changes are made to either dtos or entities
   *
   * @param systemChange object representing changes to a system
   * @return if relevant changes are present
   */
  private static boolean checkForEntityModification(SystemChange systemChange) {
    return (systemChange.getEntities().isEmpty() && systemChange.getDtos().isEmpty());
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////// REST CALLS////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



  private boolean checkServiceForCallsToApi(JService jService, String url) {
    return jService.getRestCalls().stream().anyMatch(rc -> rc.getApi().equals(url) && !rc.getDestFile().isEmpty());
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////// ENDPOINT////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Get a list of all changed endpoints for a single delta
   *
   * @param delta delta object representing changes to a system
   * @return list of endpoint changes from the given delta
   */
  public List<EndpointChange> getEndpointChangesForDelta(Delta delta) {
    List<EndpointChange> endpointChanges = new ArrayList<>();
    JController oldController;

    if (Objects.isNull(delta.getCChange())) {
      return endpointChanges;
    }

    switch (delta.getChangeType()) {
      case ADD:
        endpointChanges.addAll(compareEndpoints(null, delta.getCChange().getEndpoints(), delta));
        break;
      case MODIFY:
        oldController =
            microserviceMap.get(delta.getMsName()).getControllers().stream()
                .filter(c -> c.getClassPath().equals(delta.getLocalPath()))
                .findFirst()
                .orElse(null);

        // Handle case that there was no old controller (aka new controller) but the file still
        // existed
        endpointChanges.addAll(
            compareEndpoints(
                oldController != null ? oldController.getEndpoints() : null,
                delta.getCChange().getEndpoints(),
                delta));
        break;
      case DELETE:
        oldController =
            microserviceMap.get(delta.getMsName()).getControllers().stream()
                .filter(c -> c.getClassPath().equals(delta.getLocalPath()))
                .findFirst()
                .orElse(null);
        endpointChanges.addAll(compareEndpoints(oldController.getEndpoints(), null, delta));

        break;
    }

    return endpointChanges;
  }

  /**
   * Helper method to create a list of {@link EndpointChange} objects based on the old and new
   * {@link Endpoint} lists and delta changes for a given delta
   *
   * @param oldEndpointList list of original endpoints
   * @param newEndpointList list of new endpoints
   * @param delta set of changes to system
   * @return list of endpoint changes
   */
  private List<EndpointChange> compareEndpoints(
      List<Endpoint> oldEndpointList, List<Endpoint> newEndpointList, Delta delta) {
    List<EndpointChange> endpointChanges = new ArrayList<>();

    // Delete
    if (newEndpointList == null) {
      for (Endpoint oldEndpoint : oldEndpointList) {
        endpointChanges.add(
            new EndpointChange(
                oldEndpoint,
                null,
                getEndpointLinks(oldEndpoint, delta.getMsName()),
                new ArrayList<>(),
                delta.getChangeType()));
      }
      return endpointChanges;
    }

    // Create
    if (oldEndpointList == null) {
      for (Endpoint newEndpoint : newEndpointList) {
        endpointChanges.add(
            new EndpointChange(
                null,
                newEndpoint,
                new ArrayList<>(),
                getEndpointLinks(newEndpoint, delta.getMsName()),
                delta.getChangeType()));
      }
      return endpointChanges;
    }

    for (Endpoint oldEndpoint : oldEndpointList) {
      for (Endpoint newEndpoint : newEndpointList) {
        if (oldEndpoint.getMethodName().equals(newEndpoint.getMethodName())) {
          endpointChanges.add(
              new EndpointChange(
                  oldEndpoint,
                  newEndpoint,
                  getEndpointLinks(oldEndpoint, delta.getMsName()),
                  getEndpointLinks(newEndpoint, delta.getMsName()),
                  delta.getChangeType()));
        }
      }
    }

    return endpointChanges;
  }

  /**
   * @apiNote Austin - I don't understand what this method does. It adds a link between the service
   *     and itself??
   */
  private List<Link> getEndpointLinks(Endpoint endpoint, String microserviceName) {
    List<Link> linkList = new ArrayList<>();

    for (Microservice microservice : microserviceMap.values()) {
      if (microservice.getId().equals(microserviceName)) {
        continue;
      }

      for (JService service : microservice.getServices()) {
        for (RestCall restCall : service.getRestCalls()) {
          if (endpoint.getUrl().equals(restCall.getApi())) {
            linkList.add(new Link(microservice.getId(), microserviceName));
          }
        }
      }
    }

    return linkList;
  }
}
