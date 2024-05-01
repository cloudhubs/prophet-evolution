package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.change.EndpointChange;
import edu.university.ecs.lab.impact.models.enums.EndpointImpact;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Service to handle endpoint changes between two microservices */
public class EndpointChangeService {
  /** Map of microservices name to data in the original system */
  private final Map<String, Microservice> oldMicroserviceMap;

  /** Map of microservices name to data in the new (merged) system */
  private final Map<String, Microservice> newMicroserviceMap;

  /**
   * Constructor for EndpointChangeService
   *
   * @param oldMicroserviceMap old microservice map
   * @param newMicroserviceMap new (after merge) microservice map
   */
  public EndpointChangeService(
      Map<String, Microservice> oldMicroserviceMap, Map<String, Microservice> newMicroserviceMap) {
    this.oldMicroserviceMap = oldMicroserviceMap;
    this.newMicroserviceMap = newMicroserviceMap;
  }

  /**
   * Get a list of all changed rest calls for a single delta
   *
   * @return list of rest call changes from the given delta
   */
  public List<EndpointChange> getAllMsEndpointChanges(
      Microservice oldMicroservice, Microservice newMicroservice) {

    // Ensure non null
    Objects.requireNonNull(
        oldMicroservice, "Old microservice cannot be null during endpoint metrics collection.");
    Objects.requireNonNull(
        newMicroservice, "Old microservice cannot be null during endpoint metrics collection.");

    // Build endpoint changes
    List<EndpointChange> endpointChanges = new ArrayList<>();

    // Parse existing and deleted controllers (if deleted, newController is null)
    for (JController oldController : oldMicroservice.getControllers()) {
      JController newController =
          newMicroservice.getControllers().stream()
              .filter(oldController::matchClassPath)
              .findFirst()
              .orElse(null);
      endpointChanges.addAll(getControllerEndpointChanges(oldController, newController));
    }

    // Add changes for newly created controllers (oldController is null)
    newMicroservice.getControllers().stream()
        .filter(
            newController ->
                oldMicroservice.getControllers().stream().noneMatch(newController::matchClassPath))
        .forEach(
            newController ->
                endpointChanges.addAll(getControllerEndpointChanges(null, newController)));

    endpointChanges.forEach(this::checkBreakingDependentCall);

    List<EndpointChange> filteredChanges = filterNoChange(endpointChanges);
    return filteredChanges;
  }

  /**
   * Handle all cases for controller changes. Has logic for ADD, DELETE, else delegate to {@link
   * #getModifiedControllerEndpointChanges(JController, JController)} for MODIFY
   *
   * @param oldController controller in old IR (null if DELETE)
   * @param newController controller in new IR (null if ADD)
   * @return list of endpoint changes
   */
  private List<EndpointChange> getControllerEndpointChanges(
      JController oldController, JController newController) {
    if (Objects.isNull(oldController) || Objects.isNull(newController)) {
      boolean isDelete = Objects.isNull(newController);
      JController singleController = isDelete ? oldController : newController;

      Objects.requireNonNull(
          singleController,
          "Both controllers are null during metrics collection, this should not happen.");
      return singleController.getEndpoints().stream()
          .map(
              endpoint ->
                  EndpointChange.buildChange(
                      isDelete ? endpoint : null, isDelete ? null : endpoint))
          .collect(Collectors.toList());
    }

    // Both controllers exist, compare their endpoints
    return getModifiedControllerEndpointChanges(oldController, newController);
  }

  /**
   * Handle MODIFY case for endpoint changes
   *
   * @param oldController controller in old IR (not null)
   * @param newController controller in new IR (not null)
   * @return list of endpoint changes
   */
  private List<EndpointChange> getModifiedControllerEndpointChanges(
      JController oldController, JController newController) {
    Objects.requireNonNull(oldController, "Old controller cannot be null for modify.");
    Objects.requireNonNull(newController, "New controller cannot be null for modify.");

    // Same concept as before, but with endpoints
    List<Endpoint> oldEndpoints = oldController.getEndpoints();
    List<Endpoint> newEndpoints = newController.getEndpoints();

    List<EndpointChange> endpointChanges = new ArrayList<>();

    // Parse existing and deleted endpoints (if deleted, newEndpoint is null)
    for (Endpoint oldEndpoint : oldEndpoints) {
      Endpoint newEndpoint =
          newEndpoints.stream().filter(oldEndpoint::isSameEndpoint).findFirst().orElse(null);
      endpointChanges.add(EndpointChange.buildChange(oldEndpoint, newEndpoint));
    }

    // Add new endpoints (oldEndpoint is null)
    newEndpoints.stream()
        .filter(newEndpoint -> oldEndpoints.stream().noneMatch(newEndpoint::isSameEndpoint))
        .forEach(newEndpoint -> endpointChanges.add(EndpointChange.buildChange(null, newEndpoint)));

    return endpointChanges;
  }

  // TODO see if we can move this into the endpoint change class as a static method, and even better
  // set it during constructor
  // TODO update logic now that we have the new rest call changes
  private void checkBreakingDependentCall(EndpointChange endpointChange) {
    List<RestCall> brokenRestCalls = new ArrayList<>();

    // Not directly breaking dependents if it isn't DELETE
    if (endpointChange.getChangeType() != ChangeType.DELETE) {
      return;
    }

    String microserviceName = endpointChange.getOldEndpoint().getMsId();

    for (Microservice microservice : newMicroserviceMap.values()) {
      if (microservice.getId().equals(microserviceName)) {
        continue;
      }

      for (JService service : microservice.getServices()) {
        for (RestCall restCall : service.getRestCalls()) {
          if (restCall.getDestEndpoint().equals(endpointChange.getOldEndpoint().getUrl())) {
            brokenRestCalls.add(restCall);
          }
        }
      }
    }

    // No impact if no dependent calls were found
    if (brokenRestCalls.isEmpty()) {
      return;
    }

    endpointChange.setBrokenRestCalls(brokenRestCalls);
    endpointChange.setImpact(EndpointImpact.BROKE_DEPENDENT_CALLS);
  }

  /**
   * Filter out endpoint changes that have not changed
   *
   * @param endpointChanges list of endpoint changes
   * @return list of endpoint changes that have changed
   */
  private List<EndpointChange> filterNoChange(List<EndpointChange> endpointChanges) {
    return endpointChanges.stream()
        .filter(EndpointChange::isChanged)
        .collect(Collectors.toList());
  }
}
