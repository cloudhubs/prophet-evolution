package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.change.EndpointChange;
import edu.university.ecs.lab.impact.models.change.Link;
import edu.university.ecs.lab.impact.models.enums.EndpointImpact;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static edu.university.ecs.lab.common.utils.FlowUtils.buildFlows;

public class EndpointChangeService {
    private final Map<String, Microservice> oldMicroserviceMap;
    private final Map<String, Microservice> newMicroserviceMap;

    public EndpointChangeService(Map<String, Microservice> oldMicroserviceMap, Map<String, Microservice> newMicroserviceMap) {
        this.oldMicroserviceMap = oldMicroserviceMap;
        this.newMicroserviceMap = newMicroserviceMap;
    }

    /**
     * Get a list of all changed rest calls for a single delta
     *
     * @return list of rest call changes from the given delta
     */
    public List<EndpointChange> getAllMsEndpointChanges(Microservice oldMicroservice, Microservice newMicroservice) {

        // Ensure non null
        Objects.requireNonNull(oldMicroservice, "Old microservice cannot be null during endpoint metrics collection.");
        Objects.requireNonNull(newMicroservice, "Old microservice cannot be null during endpoint metrics collection.");

        String msId = newMicroservice.getId();

        // Find all their endpoints
        List<JController> oldControllers = oldMicroservice.getControllers();
        List<JController> newControllers = newMicroservice.getControllers();


        // Build endpoint changes
        List<EndpointChange> endpointChanges = new ArrayList<>();

        // Parse existing and deleted controllers (if deleted, newController is null)
        for (JController oldController : oldControllers) {
            JController newController = newControllers.stream().filter(oldController::matchClassPath).findFirst().orElse(null);

            List<EndpointChange> controllerChanges = getControllerEndpointChanges(oldController, newController);
            endpointChanges.addAll(controllerChanges);
        }

        // Add changes for newly created controllers (oldController is null)
        newControllers.stream()
                .filter(newController -> oldControllers.stream().noneMatch(newController::matchClassPath))
                .forEach(newController -> {
                    List<EndpointChange> controllerChanges = getControllerEndpointChanges(null, newController);
                    endpointChanges.addAll(controllerChanges);
                });

        updateEndpointChangeImpact(endpointChanges, msId);

        return filterNoImpact(endpointChanges);
    }

    /**
     * Handle all cases for controller changes. Has logic for ADD, DELETE,
     * else delegate to {@link #getModifiedControllerEndpointChanges(JController, JController)} for MODIFY
     * @param oldController controller in old IR (null if DELETE)
     * @param newController controller in new IR (null if ADD)
     * @return list of endpoint changes
     */
    private List<EndpointChange> getControllerEndpointChanges(JController oldController, JController newController) {
        if (Objects.isNull(oldController) || Objects.isNull(newController)) {
            boolean isDelete = Objects.isNull(newController);
            JController singleController = isDelete ? oldController : newController;

            Objects.requireNonNull(singleController, "Both controllers are null during metrics collection, this should not happen.");
            return singleController.getEndpoints().stream()
                    .map(endpoint ->
                            EndpointChange.addOrDeleteControllerChange(
                                    endpoint,
                                    getEndpointLinks(endpoint, singleController.getMsId(), isDelete),
                                    isDelete ? ChangeType.DELETE : ChangeType.ADD))
                    .collect(Collectors.toList());
        }

        // Both controllers exist, compare their endpoints
        return getModifiedControllerEndpointChanges(oldController, newController);

    }

    /**
     * Handle MODIFY case for endpoint changes
     * @param oldController controller in old IR (not null)
     * @param newController  controller in new IR (not null)
     * @return list of endpoint changes
     */
    private List<EndpointChange> getModifiedControllerEndpointChanges(JController oldController, JController newController) {
        Objects.requireNonNull(oldController, "Old controller cannot be null for modify.");
        Objects.requireNonNull(newController, "New controller cannot be null for modify.");

        // Same concept as before, but with endpoints
        List<Endpoint> oldEndpoints = oldController.getEndpoints();
        List<Endpoint> newEndpoints = newController.getEndpoints();

        List<EndpointChange> endpointChanges = new ArrayList<>();

        // Parse existing and deleted endpoints (if deleted, newEndpoint is null)
        for (Endpoint oldEndpoint : oldEndpoints) {
            Endpoint newEndpoint = newEndpoints.stream().filter(oldEndpoint::isSameEndpoint).findFirst().orElse(null);
            endpointChanges.add(EndpointChange.buildChange(oldEndpoint, newEndpoint));
        }

        // Add new endpoints (oldEndpoint is null)
        newEndpoints.stream()
                .filter(newEndpoint -> oldEndpoints.stream().noneMatch(newEndpoint::isSameEndpoint))
                .forEach(newEndpoint -> endpointChanges.add(EndpointChange.buildChange(null, newEndpoint)));

        return endpointChanges;
    }


    private void updateEndpointChangeImpact(List<EndpointChange> endpointChangeList, String microserviceName) {
        // Check for CALL_TO_DEPRECATED_ENDPOINT
        for(EndpointChange endpointChange : endpointChangeList) {
            if (Objects.isNull(endpointChange.getOldEndpoint()) || Objects.isNull(endpointChange.getNewEndpoint())) {
                continue;
            }
            if(!checkInconsistentEndpoint(endpointChange)) {
                if (!checkUnusedCall(endpointChange, microserviceName)) {
                    checkBreakingDependentCall(endpointChange, microserviceName);
                }
            }
        }
    }


    /**
     * Get's all links that exist between the endpoint and services that call
     * this endpoint
     *
     * @param endpoint
     * @param microserviceName
     * @return
     */
    // TODO lets just store this in the endpoint object as we parse it in the delta
    private List<Link> getEndpointLinks(Endpoint endpoint, String microserviceName, boolean oldMap) {
        List<Link> linkList = new ArrayList<>();

        for (Microservice microservice : (oldMap ? oldMicroserviceMap.values() : newMicroserviceMap.values())) {
            if (microservice.getId().equals(microserviceName)) {
                continue;
            }

            for (JService service : microservice.getServices()) {
                for (RestCall restCall : service.getRestCalls()) {
                    if (endpoint.getUrl().equals(restCall.getDestEndpoint())) {
                        linkList.add(new Link(microservice.getId(), microserviceName));
                    }
                }
            }
        }

        return linkList;
    }

    private boolean checkBreakingDependentCall(EndpointChange endpointChange, String microserviceName) {
        List<RestCall> brokenRestCalls = new ArrayList<>();

        // Not directly breaking dependents if it isn't a delete
        if(endpointChange.getChangeType() != ChangeType.DELETE) {
            return false;
        }

        for(Microservice microservice : newMicroserviceMap.values()) {
            if(microservice.getId().equals(microserviceName)) {
                continue;
            }

            for(JService service : microservice.getServices()) {
                for(RestCall restCall : service.getRestCalls()) {
                    if(restCall.getDestEndpoint().equals(endpointChange.getOldEndpoint().getUrl())) {
                        brokenRestCalls.add(restCall);
                    }
                }
            }
        }


        // No impact if no dependent calls were found
        if(brokenRestCalls.isEmpty()) {
            return false;
        }


        endpointChange.setBrokenRestCalls(brokenRestCalls);
        endpointChange.setImpact(EndpointImpact.BREAKING_DEPENDENT_CALL);

        return true;
    }

    private boolean checkUnusedCall(EndpointChange endpointChange, String microserviceName) {
        /*
            If we remove an endpoint that make a call to a service with an api call that will no longer be called
            TODO Must be done via flows so WIP, no way to easily create flows from delta change + IR (must merge probably)
         */
        if(endpointChange.getChangeType() != ChangeType.DELETE) {
            return false;
        }

        Microservice oldMicroservice = oldMicroserviceMap.get(microserviceName);
        Microservice newMicroservice = oldMicroserviceMap.get(microserviceName);

        List<Flow> oldFlows = buildFlows(oldMicroservice);
        List<Flow> newFlows = buildFlows(newMicroservice);

        for(Flow flow : oldFlows) {
            // If the flow contains the same controller methodName as the endpoint deleted && it calls a service method
            if(flow.getControllerMethod().getMethodName().equals(endpointChange.getOldEndpoint().getMethodName())
            && Objects.nonNull(flow.getServiceMethodCall()) && Objects.nonNull(flow.getService())) {

                // If we find a restcall whose parent is the same service method called in the flow, it is now cut off
                // TODO assumption here is only one endpoint calls a service method, not necessarily true
                for(RestCall restCall : ((JService) flow.getService()).getRestCalls()) {
                    if(restCall.getMsId().equals(flow.getServiceMethod().getMethodName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private boolean checkInconsistentEndpoint(EndpointChange endpointChange) {
        if(endpointChange.getChangeType() != ChangeType.MODIFY) {
            return false;
        }

        if(Objects.equals(endpointChange.getOldEndpoint().getUrl(), endpointChange.getNewEndpoint().getUrl())
        && checkParameterEquivalence(endpointChange.getOldEndpoint().getParameterList(), endpointChange.getNewEndpoint().getParameterList())
        && Objects.equals(endpointChange.getOldEndpoint().getReturnType(), endpointChange.getNewEndpoint().getReturnType())) {
            return false;
        }

        endpointChange.setImpact(EndpointImpact.INCONSISTENT_ENDPOINT);
        return true;
    }

    // TODO technically this isn't accurate, we can say if the same parameters but different order isn't really a change
    private boolean checkParameterEquivalence(String paramListOld, String paramListNew) {

        return Objects.equals(paramListOld, paramListNew);
    }

    private List<EndpointChange> filterNoImpact(List<EndpointChange> endpointChanges) {
        return endpointChanges.stream().filter(endpointChange -> !(endpointChange.getImpact() == EndpointImpact.NONE)).collect(Collectors.toList());
    }
}
