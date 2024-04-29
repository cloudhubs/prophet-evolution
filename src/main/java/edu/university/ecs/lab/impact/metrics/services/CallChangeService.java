package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.enums.RestCallImpact;

import java.util.*;
import java.util.stream.Collectors;

public class CallChangeService {

    private Map<String, Microservice> oldMicroserviceMap;
    private Map<String, Microservice> newMicroserviceMap;

    // Cycle stuff
    private Map<String, Integer> microserviceKey;

    private final int vertices;
    private final List<List<Integer>> adjList;
    private int[] parent;  // To keep track of the path


    SystemChange systemChange;

    public CallChangeService(Map<String, Microservice> oldMicroserviceMap, Map<String, Microservice> newMicroserviceMap, SystemChange systemChange) {
        this.oldMicroserviceMap = oldMicroserviceMap;
        this.newMicroserviceMap = newMicroserviceMap;
        this.systemChange = systemChange;
        vertices = newMicroserviceMap.size();
        this.microserviceKey = new HashMap<>(vertices);
        this.adjList = new ArrayList<>(vertices);
        this.parent = new int[vertices];

        initializeKeyAndAdjlist();
    }

    private void initializeKeyAndAdjlist() {
        // Init the keys
        int i = 0;
        for(Microservice microservice : newMicroserviceMap.values()) {
            microserviceKey.put(microservice.getId(), i);
            i++;
        }

        // Initialize the adjList
        for(int k = 0; k < vertices; k++) {
            adjList.add(new ArrayList<>());
        }

        // Create the links
        for(Microservice microservice : newMicroserviceMap.values()) {
            for(JService service : microservice.getServices()) {
                for(RestCall restCall : service.getRestCalls()) {
                    if(Objects.nonNull(restCall.getDestMsId()) && !restCall.getDestMsId().isEmpty()) {
                        Microservice destMicroservice = oldMicroserviceMap.get(restCall.getDestMsId());
                        if(Objects.nonNull(destMicroservice)) {
                            adjList.get(microserviceKey.get(microservice.getId())).add(microserviceKey.get(destMicroservice.getId()));
                        }
                    }
                }
            }
        }
    }

    public List<String> findCycle() {
        boolean[] visited = new boolean[vertices];
        boolean[] recStack = new boolean[vertices];

        for (int node = 0; node < vertices; node++) {
            if (!visited[node] && detectCycle(node, visited, recStack)) {
                return getCycle(node);
            }
        }
        return new ArrayList<>(); // No cycle found
    }

    private boolean detectCycle(int vertex, boolean[] visited, boolean[] recStack) {
        if (recStack[vertex]) {
            return true;
        }
        if (visited[vertex]) {
            return false;
        }

        visited[vertex] = true;
        recStack[vertex] = true;

        List<Integer> children = adjList.get(vertex);
        for (Integer child : children) {
            parent[child] = vertex; // Track the path
            if (detectCycle(child, visited, recStack)) {
                return true;
            }
        }

        recStack[vertex] = false;
        return false;
    }

    private List<String> getCycle(int start) {
        List<String> cycle = new ArrayList<>();
        int current = start;
        cycle.add(keyFromValue(start));
        while (parent[current] != start) {
            cycle.add(keyFromValue(parent[current]));
            current = parent[current];
        }
        cycle.add(keyFromValue(start)); // To show the cycle completion
        Collections.reverse(cycle); // To show the cycle in correct order
        return cycle;
    }

    public boolean isInCycle(Microservice microservice) {
        return findCycle().contains(microservice.getId());
    }

    private String keyFromValue(int i) {
        for(Map.Entry<String, Integer> entry : microserviceKey.entrySet()) {
            if(entry.getValue() == i) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Get a list of all changed rest calls for a single microservice
     *
     * @return list of rest call changes from the given delta
     */
    public List<CallChange> getAllMsRestCallChanges(String microserviceName) {

        // Find the microservices
        Microservice oldMicroservice = oldMicroserviceMap.values().stream().filter(microservice -> microservice.getId().equals(microserviceName)).findFirst().orElse(null);
        Microservice newMicroservice = newMicroserviceMap.values().stream().filter(microservice -> microservice.getId().equals(microserviceName)).findFirst().orElse(null);

        // Ensure non null
        assert Objects.nonNull(oldMicroservice) && Objects.nonNull(newMicroservice);

        // Find all their rest calls
        List<RestCall> oldRestCalls = oldMicroservice.getServices().stream().flatMap(jService -> jService.getRestCalls().stream()).collect(Collectors.toList());
        List<RestCall> newRestCalls = newMicroservice.getServices().stream().flatMap(jService -> jService.getRestCalls().stream()).collect(Collectors.toList());


        // Build call changes
        List<CallChange> callChanges = new ArrayList<>();

        for (RestCall oldCall : oldRestCalls) {
            if (!newRestCalls.remove(oldCall)) {
                // If no call removed, it isn't present (removed)
                callChanges.add(new CallChange(oldCall, null, ChangeType.DELETE));
            }
        }

        for (RestCall newCall : newRestCalls) {
            if (!oldRestCalls.remove(newCall)) {
                // If no call was removed, it isn't present (added)
                callChanges.add(new CallChange(null, newCall, ChangeType.ADD));
            }
        }

        updateCallChangeImpact(callChanges, microserviceName);

        return callChanges;
    }

    private void updateCallChangeImpact(List<CallChange> callChangeList, String microserviceName) {
        for(CallChange callChange : callChangeList) {
            if(checkCallToDeprecatedEndpoint(callChange)) {
                break;
            } else if(checkUnusedEndpoint(callChange)) {
                break;
            } else if(checkAboveCouplingThreshold(callChange, microserviceName)) {
                break;
            }

        }
    }

    /*
        Check if there is at least 1 other restcall making a call to the same endpoint
     */
    private boolean checkUnusedEndpoint(CallChange callChange) {
        // If the change to our RestCall isn't a delete
        if(callChange.getChangeType() == ChangeType.ADD) {
            return false;
        }

        // Check microservice system
        for (Microservice microservice : newMicroserviceMap.values()) {
            for (JService service : microservice.getServices()) {
                for (RestCall restCall : service.getRestCalls()) {
                    // Does the restCall hit the same api as our old (now deleted) restCall
                    if (restCall.getDestEndpoint().equals(callChange.getOldCall().getDestEndpoint())) {
                        return false;
                    }
                }
            }
        }

        // If not one is found, the endpoint is now unused
        callChange.setImpact(RestCallImpact.UNUSED_ENDPOINT);
        return true;
    }

    /*
        Check if any endpoint matches new rest call api
     */
    private boolean checkCallToDeprecatedEndpoint(CallChange callChange) {
        // If the change to our RestCall is a delete, return false
        if(callChange.getChangeType() == ChangeType.DELETE) {
            return false;
        }

        // If we find an endpoint advertising the called api we can return false
        for (Microservice microservice : newMicroserviceMap.values()) {
            for (JController controller : microservice.getControllers()) {
                for (Endpoint endpoint : controller.getEndpoints()) {
                    if (endpoint.getUrl().equals(callChange.getNewCall().getDestEndpoint())) {
                        return false;
                    }
                }
            }
        }

        // If no matching url is found, we are calling deprecated/nonexistent endpoint
        callChange.setImpact(RestCallImpact.CALL_TO_DEPRECATED_ENDPOINT);
        return true;
    }

    public boolean checkAboveCouplingThreshold(CallChange callChange, String microserviceName) {
        if(callChange.getChangeType() != ChangeType.ADD) {
            return false;
        }

        Microservice oldMicroservice = newMicroserviceMap.get(microserviceName);
        Microservice newMicroservice = newMicroserviceMap.get(microserviceName);
        int oldADS = MicroserviceMetricsService.calculateADS(oldMicroservice);
        int newADS = MicroserviceMetricsService.calculateADS(newMicroservice);

        // If our ADS (# of links) went down or remains the same
        if (oldADS >= newADS || MicroserviceMetricsService.THRESHOLD > newADS) {
            return false;
        }

        // Otherwise if our ADS went up, and it's now above the threshold, we will blame all new valid add's
        callChange.setImpact(RestCallImpact.HIGH_COUPLING);
        return true;
    }
}
