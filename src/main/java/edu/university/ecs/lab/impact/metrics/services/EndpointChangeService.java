package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.change.EndpointChange;
import edu.university.ecs.lab.impact.models.change.Link;
import edu.university.ecs.lab.impact.models.enums.EndpointImpact;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static edu.university.ecs.lab.common.utils.FlowUtils.buildFlows;

public class EndpointChangeService {
    Map<String, Microservice> oldMicroserviceMap;
    Map<String, Microservice> newMicroserviceMap;

    SystemChange systemChange;

    public EndpointChangeService(Map<String, Microservice> oldMicroserviceMap, Map<String, Microservice> newMicroserviceMap, SystemChange systemChange) {
        this.oldMicroserviceMap = oldMicroserviceMap;
        this.newMicroserviceMap = newMicroserviceMap;
        this.systemChange = systemChange;
    }
    /**
     * Get a list of all changed rest calls for a single delta
     *
     * @param delta delta object representing changes to a system
     * @return list of rest call changes from the given delta
     */
    public List<EndpointChange> getAllMsEndpointChanges(String microserviceName) {
        List<EndpointChange> changes = new ArrayList<>();

        // Find the microservices
        Microservice oldMicroservice = oldMicroserviceMap.get(microserviceName);
        Microservice newMicroservice = newMicroserviceMap.get(microserviceName);

        // Ensure non null
        assert Objects.nonNull(oldMicroservice) && Objects.nonNull(newMicroservice);



        // Find all their endpoints
        List<JController> oldControllers = oldMicroservice.getControllers();
        List<JController> newControllers = oldMicroservice.getControllers();


        // Build endpoint changes
        List<EndpointChange> endpointChanges = new ArrayList<>();

        // Handle deleted classes
        for (JController oldController : oldControllers) {
            if(newControllers.stream().filter(jController -> jController.getClassPath().equals(oldController.getClassPath())).findFirst().isEmpty()) {
                for(Endpoint oldEndpoint : oldController.getEndpoints()) {
                    // Add the new endpoint change of delete
                    endpointChanges.add(new EndpointChange(
                            oldEndpoint,
                            null,
                            getEndpointLinks(oldEndpoint, microserviceName, true),
                            new ArrayList<>(),
                            ChangeType.DELETE));
                }
            }
        }

        // Handle added classes
        for (JController newController : newControllers) {
            if(newControllers.stream().filter(jController -> jController.getClassPath().equals(newController.getClassPath())).findFirst().isEmpty()) {
                for(Endpoint newEndpoint : newController.getEndpoints()) {
                    // Add the new endpoint change of delete
                    endpointChanges.add(new EndpointChange(
                            null,
                            newEndpoint,
                            new ArrayList<>(),
                            getEndpointLinks(newEndpoint, microserviceName, false),
                            ChangeType.ADD));
                }
            }
        }

        // Handle deleted classes
        for (JController oldController : oldControllers) {
            if(newControllers.stream().filter(jController -> jController.getClassPath().equals(oldController.getClassPath())).findFirst().isPresent()) {
                JController newController = newControllers.stream().filter(jController -> jController.getClassPath().equals(oldController.getClassPath())).findFirst().get();

                // Same concept as before, but with endpoints
                List<Endpoint> oldEndpoints = oldController.getEndpoints();
                List<Endpoint> newEndpoints = newController.getEndpoints();

                // Handle deleted endpoints
                for (Endpoint oldEndpoint : oldEndpoints) {
                    if(newEndpoints.stream().filter(endpoint -> endpoint.getMethodName().equals(oldEndpoint.getMethodName())).findFirst().isEmpty()) {
                        // Add the new endpoint change of delete
                        endpointChanges.add(new EndpointChange(
                                oldEndpoint,
                                null,
                                getEndpointLinks(oldEndpoint, microserviceName, true),
                                new ArrayList<>(),
                                ChangeType.DELETE));
                    }
                }

                // Handle added endpoints
                for (Endpoint newEndpoint : newEndpoints) {
                    if(oldEndpoints.stream().filter(endpoint -> endpoint.getMethodName().equals(newEndpoint.getMethodName())).findFirst().isEmpty()) {
                        // Add the new endpoint change of delete
                        endpointChanges.add(new EndpointChange(
                                null,
                                newEndpoint,
                                new ArrayList<>(),
                                getEndpointLinks(newEndpoint, microserviceName, false),
                                ChangeType.ADD));
                    }
                }

                // Handle modified endpoints
                for (Endpoint oldEndpoint : oldEndpoints) {
                    if(newEndpoints.stream().filter(endpoint -> endpoint.getMethodName().equals(oldEndpoint.getMethodName())).findFirst().isPresent()) {
                        Endpoint newEndpoint = newEndpoints.stream().filter(endpoint -> endpoint.getMethodName().equals(oldEndpoint.getMethodName())).findFirst().get();
                        // Add the new endpoint change of delete
                        endpointChanges.add(new EndpointChange(
                                oldEndpoint,
                                newEndpoint,
                                getEndpointLinks(oldEndpoint, microserviceName, true),
                                getEndpointLinks(newEndpoint, microserviceName, false),
                                ChangeType.DELETE));
                    }
                }

            }
        }

        updateEndpointChangeImpact(endpointChanges, microserviceName);

        return changes;
    }



    private void updateEndpointChangeImpact(List<EndpointChange> endpointChangeList, String microserviceName) {
        // Check for CALL_TO_DEPRECATED_ENDPOINT
        for(EndpointChange endpointChange : endpointChangeList) {
            if(checkInconsistentEndpoint(endpointChange)) {
                break;
            } else if(checkUnusedCall(endpointChange, microserviceName)) {
                break;
            } else if(checkBreakingDependentCall(endpointChange, microserviceName)) {
                break;
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
    private List<Link> getEndpointLinks(Endpoint endpoint, String microserviceName, boolean oldMap) {
        List<Link> linkList = new ArrayList<>();

        for (Microservice microservice : (oldMap ? oldMicroserviceMap.values() : newMicroserviceMap.values())) {
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
                    if(restCall.getApi().equals(endpointChange.getOldEndpoint().getUrl())) {
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

        Microservice microservice = oldMicroserviceMap.get(microserviceName);
        List<Flow> flows = buildFlows(microservice);

        for(Flow flow : flows) {
            // If the flow contains the same controller methodName as th endpoint deleted && it calls a service method
            if(flow.getControllerMethod().getMethodName().equals(endpointChange.getOldEndpoint().getMethodName())
            && Objects.nonNull(flow.getServiceMethodCall())) {

                // If we find a restcall whose parent is the same service method called in the flow, it is now cut off
                // TODO assumption here is only one endpoint calls a service method, not necessarily true
                for(RestCall restCall : flow.getService().getRestCalls()) {
                    if(restCall.getParentMethod().equals(flow.getServiceMethod().getMethodName())) {
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
}
