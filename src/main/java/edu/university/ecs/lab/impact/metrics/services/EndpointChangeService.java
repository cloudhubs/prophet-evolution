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

public class EndpointChangeService {
    Map<String, Microservice> microserviceMap;
    SystemChange systemChange;

    public EndpointChangeService(Map<String, Microservice> microserviceMap, SystemChange systemChange) {
        this.microserviceMap = microserviceMap;
        this.systemChange = systemChange;
    }

    /**
     * Get a list of all changed rest calls for a single delta
     *
     * @param delta delta object representing changes to a system
     * @return list of rest call changes from the given delta
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

        updateEndpointChangeImpact(endpointChanges);


        return endpointChanges;
    }

    private void updateEndpointChangeImpact(List<EndpointChange> endpointChangeList) {
        // Check for CALL_TO_DEPRECATED_ENDPOINT
        for(EndpointChange endpointChange : endpointChangeList) {
            if(checkInconsistentEndpoint(endpointChange)) {
                break;
            } else if(checkUnusedCall(endpointChange)) {
                break;
            } else if(checkBreakingDependentCall(endpointChange)) {
                break;
            }

        }
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
     * Get's all links that exist between the endpoint and services that call
     * this endpoint
     *
     * @param endpoint
     * @param microserviceName
     * @return
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

    private boolean checkBreakingDependentCall(EndpointChange endpointChange) {
        List<RestCall> brokenRestCalls = new ArrayList<>();

//        if(endpointChange.getChangeType() != ChangeType.ADD) {
//            return brokenRestCalls;
//        }

        // Check the delta file for added/modified/deleted services
        List<Delta> addOrModifiedServices = systemChange.getServices().stream().filter(s -> s.getChangeType() == ChangeType.MODIFY || s.getChangeType() == ChangeType.ADD).collect(Collectors.toList());
        List<String> deletedServiceClasspaths = systemChange.getServices().stream().filter(s -> s.getChangeType() == ChangeType.DELETE).map(s -> s.getSChange().getClassPath()).collect(Collectors.toList());

        // Find any dependent calls from added/modified
        for(Delta delta : addOrModifiedServices) {
            for(RestCall restCall : delta.getSChange().getRestCalls()) {
                if(restCall.getApi().equals(endpointChange.getOldEndpoint().getUrl())) {
                    brokenRestCalls.add(restCall);
                }
            }
        }

        List<String> addOrModifiedClasspaths = addOrModifiedServices.stream().map(s -> s.getSChange().getClassPath()).collect(Collectors.toList());

        // Now we check the map not including what exists in the delta file
        for(JService jService : microserviceMap.values().stream().flatMap(microservice -> microservice.getServices().stream()).collect(Collectors.toList())) {
            for(RestCall restCall : jService.getRestCalls()) {

                // If it exists in the map and it isn't a removed service as of the delta change
                // also don't check duplicate files we already checked in delta
                if(restCall.getApi().equals(endpointChange.getOldEndpoint().getUrl())
                && !deletedServiceClasspaths.contains(jService.getClassPath())
                && !addOrModifiedClasspaths.contains(jService.getClassPath())) {
                    brokenRestCalls.add(restCall);
                }
            }
        }

        // If we have no broken (dependent) calls then no impact
        if(brokenRestCalls.isEmpty()) {
            return false;
        }

        endpointChange.setBrokenRestCalls(brokenRestCalls);
        endpointChange.setImpact(EndpointImpact.BREAKING_DEPENDENT_CALL);

        return true;
    }

    private boolean checkUnusedCall(EndpointChange endpointChange) {
        /*
            If we remove an endpoint that make a call to a service with an api call that will no longer be called
            TODO Must be done via flows so WIP, no way to easily create flows from delta change + IR (must merge probably)
         */

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
