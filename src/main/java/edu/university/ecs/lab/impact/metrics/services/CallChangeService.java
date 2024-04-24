package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.enums.RestCallImpact;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CallChangeService {

    Map<String, Microservice> microserviceMap;
    SystemChange systemChange;

    public CallChangeService(Map<String, Microservice> microserviceMap, SystemChange systemChange) {
        this.microserviceMap = microserviceMap;
        this.systemChange = systemChange;
    }

    /**
     * Get a list of all changed rest calls for a single delta
     *
     * @param delta delta object representing changes to a system
     * @return list of rest call changes from the given delta
     */
    public List<CallChange> getAllRestCallChangesForService(Microservice microservice) {
        List<CallChange> changes = new ArrayList<>();
        for(Delta delta : systemChange.getServices()) {
            if(delta.getMsName().equals(microservice.getId())) {
                changes.addAll(getRestCallChangesForDelta(delta));
            }
        }


        return changes;
    }

    /**
     * Get a list of all changed rest calls for a single delta
     *
     * @param delta delta object representing changes to a system
     * @return list of rest call changes from the given delta
     */
    public List<CallChange> getRestCallChangesForDelta(Delta delta) {
        List<CallChange> callChanges = new ArrayList<>();
        JService oldService;

        if (Objects.isNull(delta.getSChange())) {
            return callChanges;
        }

        switch (delta.getChangeType()) {
            case ADD:
                callChanges.addAll(compareRestCalls(null, delta.getSChange()));

                break;
            case MODIFY:
                oldService =
                        microserviceMap.get(delta.getMsName()).getServices().stream()
                                .filter(s -> s.getClassPath().equals(delta.getLocalPath()))
                                .findFirst()
                                .orElse(null);
                callChanges.addAll(compareRestCalls(oldService, delta.getSChange()));

                break;
            case DELETE:
                oldService =
                        microserviceMap.get(delta.getMsName()).getServices().stream()
                                .filter(s -> s.getClassPath().equals(delta.getLocalPath()))
                                .findFirst()
                                .orElse(null);
                callChanges.addAll(compareRestCalls(oldService, null));

                break;
        }

        updateCallChangeImpact(callChanges);


        return callChanges;
    }

    /**
     * Compare all restCalls in oldService to newService, NOTE: no concept of 'modified' calls there
     * are only oldLinks and newLinks
     *
     * @param oldService old service object
     * @param newService new service object
     * @return list of changes to rest calls
     */
    private List<CallChange> compareRestCalls(
            JService oldService, JService newService) {
        List<CallChange> callChanges = new ArrayList<>();

        // If it is an added class
        if (oldService == null) {
            for (RestCall newCall : newService.getRestCalls()) {
                callChanges.add(new CallChange(null, updateRestCallDest(newCall), ChangeType.ADD));
            }

            return callChanges;
        }

        // If it is a deleted class
        if (newService == null) {
            for (RestCall oldCall : oldService.getRestCalls()) {
                callChanges.add(new CallChange(oldCall, null, ChangeType.DELETE));
            }

            return callChanges;
        }

        List<RestCall> oldRestCalls = oldService.getRestCalls();
        List<RestCall> newRestCalls = newService.getRestCalls();

        for (RestCall oldCall : oldRestCalls) {
            if (!newRestCalls.remove(oldCall)) {
                // If no call removed, it isn't present (removed)
                callChanges.add(new CallChange(oldCall, null, ChangeType.DELETE));
            }
        }

        for (RestCall newCall : newRestCalls) {
            if (!oldRestCalls.remove(newCall)) {
                // If no call was removed, it isn't present (added)
                callChanges.add(new CallChange(null, updateRestCallDest(newCall), ChangeType.ADD));
            }
        }

        return callChanges;
    }

    private RestCall updateRestCallDest(RestCall restCall) {
        for (Microservice microservice : microserviceMap.values()) {
            for (JController controller : microservice.getControllers()) {
                for (Endpoint endpoint : controller.getEndpoints()) {
                    if (endpoint.getUrl().equals(restCall.getApi())) {
                        restCall.setDestFile(controller.getClassName());
                    }
                }
            }
        }

        return restCall;
    }

    private void updateCallChangeImpact(List<CallChange> callChangeList) {
        // Check for CALL_TO_DEPRECATED_ENDPOINT
        for(CallChange callChange : callChangeList) {
            if(checkCallToDeprecatedEndpoint(callChange)) {
                break;
            } else if(checkUnusedEndpoint(callChange)) {
                break;
            }

        }
    }

    private boolean checkUnusedEndpoint(CallChange callChange) {
        // If the change to our RestCall is a delete or add
        if(callChange.getChangeType() == ChangeType.DELETE) {
            String targetClassPath = callChange.getOldCall().getDestFile();

            // Look in delta changes for the targetFile if it exists
            Delta deltaTarget = systemChange.getControllers().stream().filter(c -> c.getLocalPath().equals(targetClassPath)).findFirst().orElse(null);

            // Check recent delta changes
            if(Objects.nonNull(deltaTarget) && deltaTarget.getChangeType() == ChangeType.ADD) {
                for (Delta delta : systemChange.getControllers()) {
                    for (RestCall restCall : delta.getSChange().getRestCalls()) {
                        // If a call is made in some other delta change
                        if (restCall.getApi().equals(callChange.getOldCall().getApi())) {
                            return false;
                        }
                    }
                }
            }

        } else {
            // Else we are done with this check because it was an added call
            return false;
        }

        // Check microservice system
        for (Microservice microservice : microserviceMap.values()) {
            for (JService service : microservice.getServices()) {
                for (RestCall restCall : service.getRestCalls()) {
                    if (restCall.getApi().equals(callChange.getOldCall().getApi()) && restCall != callChange.getOldCall()) {
                        return false;
                    }
                }
            }
        }

        callChange.setImpact(RestCallImpact.UNUSED_ENDPOINT);
        return true;
    }

    private boolean checkCallToDeprecatedEndpoint(CallChange callChange) {
        // If the change to our RestCall is a add or modify
        if(callChange.getChangeType() == ChangeType.ADD) {
            String targetClassPath = callChange.getNewCall().getDestFile();

            // Look in delta changes for the targetFile if it exists
            Delta deltaTarget = systemChange.getControllers().stream().filter(c -> c.getLocalPath().equals(targetClassPath)).findFirst().orElse(null);

            if (Objects.nonNull(deltaTarget)) {
                if ((deltaTarget.getChangeType() == ChangeType.ADD || deltaTarget.getChangeType() == ChangeType.MODIFY)) {
                    for (Endpoint endpoint : deltaTarget.getCChange().getEndpoints()) {
                        if (endpoint.getUrl().equals(callChange.getNewCall().getApi())) {
                            return false;
                        }
                    }

                    callChange.setImpact(RestCallImpact.CALL_TO_DEPRECATED_ENDPOINT);
                    return true;
                } else {
                    callChange.setImpact(RestCallImpact.CALL_TO_DEPRECATED_ENDPOINT);
                    return true;
                }
            } else {

                // Check Microservice system
                for (Microservice microservice : microserviceMap.values()) {
                    for (JController controller : microservice.getControllers()) {
                        for (Endpoint endpoint : controller.getEndpoints()) {
                            if (endpoint.getUrl().equals(callChange.getNewCall().getApi())) {
                                return false;
                            }
                        }
                    }
                }
                callChange.setImpact(RestCallImpact.CALL_TO_DEPRECATED_ENDPOINT);
                return true;
            }
        }

        return false;
    }
}
