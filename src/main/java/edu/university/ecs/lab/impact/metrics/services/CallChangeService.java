package edu.university.ecs.lab.impact.metrics.services;

import com.google.common.graph.Graph;
import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.metrics.services.cyclic.MicroserviceGraph;
import edu.university.ecs.lab.impact.metrics.services.cyclic.node.Link;
import edu.university.ecs.lab.impact.metrics.services.cyclic.node.Node;
import edu.university.ecs.lab.impact.metrics.services.cyclic.node.Request;
import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.enums.RestCallImpact;

import java.util.*;
import java.util.stream.Collectors;

public class CallChangeService {

  private final Map<String, Microservice> oldMicroserviceMap;
  private final Map<String, Microservice> newMicroserviceMap;
  private final Map<String, List<Set<Node>>> cyclicDependencyMap;


  public CallChangeService(
      Map<String, Microservice> oldMicroserviceMap, Map<String, Microservice> newMicroserviceMap) {
    this.oldMicroserviceMap = oldMicroserviceMap;
    this.newMicroserviceMap = newMicroserviceMap;
    this.cyclicDependencyMap = getCyclicDependencies();
  }


  /**
   * Get a list of all changed rest calls for a single microservice
   *
   * @return list of rest call changes from the given delta
   */
  public List<CallChange> getMsRestCallChanges(Microservice oldService, Microservice newService) {
    // Ensure non null TODO make work for either deleted old or new service
    assert Objects.nonNull(oldService) && Objects.nonNull(newService);

    // Find all their rest calls
    List<RestCall> oldRestCalls =
        oldService.getServices().stream()
            .flatMap(jService -> jService.getRestCalls().stream())
            .collect(Collectors.toList());
    List<RestCall> newRestCalls =
        newService.getServices().stream()
            .flatMap(jService -> jService.getRestCalls().stream())
            .collect(Collectors.toList());

    // Build call changes
    List<CallChange> callChanges = new ArrayList<>();

    updateCallChangeImpact(callChanges, newService.getId());
    for (RestCall oldCall : oldRestCalls) {
      if (!newRestCalls.remove(oldCall)) {
        // If no call removed, it isn't present (removed)
        callChanges.add(CallChange.buildChange(oldCall, null));
      }
    }

    for (RestCall newCall : newRestCalls) {
      if (!oldRestCalls.remove(newCall)) {
        // If no call was removed, it isn't present (added)
        callChanges.add(CallChange.buildChange(null, newCall));
      }
    }

    updateCallChangeImpact(callChanges, oldService.getId());

    return callChanges;
  }

  private void updateCallChangeImpact(List<CallChange> callChangeList, String microserviceName) {
    for (CallChange callChange : callChangeList) {
      if (checkCallToDeprecatedEndpoint(callChange)) {
        break;
      } else if (checkUnusedEndpoint(callChange)) {
        break;
      } else if (checkAboveCouplingThreshold(callChange, microserviceName)) {
        break;
      }
      // TODO Cyclic
//      else if (cyclicDependencyMap.get(microserviceName)) {
//
//      }
    }
  }

  /*

   */
  // TODO Cyclic Note: Need to add links to node? So we can see requests which contain information to identify callchange
//  private boolean checkMemberOfCyclic(CallChange callChange, String microserviceName) {
//    if(!cyclicDependencyMap.containsKey(microserviceName)) {
//      return false;
//    }
//
//    List<Set<Node>> cyclicDependencies = cyclicDependencyMap.get(microserviceName);
//
//    for(Set<Node> cyclicDependency : cyclicDependencies) {
//      for(Node node : cyclicDependency) {
//
//      }
//    }
//  }

  /*
     Check if there is at least 1 other restcall making a call to the same endpoint
  */
  private boolean checkUnusedEndpoint(CallChange callChange) {
    // If the change to our RestCall isn't a DELETE
    if (callChange.getChangeType() == ChangeType.ADD) {
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
    callChange.setImpact(RestCallImpact.CAUSE_UNUSED_ENDPOINT);
    return true;
  }

  /*
     Check if any endpoint matches new rest call api
  */
  private boolean checkCallToDeprecatedEndpoint(CallChange callChange) {
    // If the change to our RestCall is a delete, return false
    if (callChange.getChangeType() == ChangeType.DELETE) {
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
    if (callChange.getChangeType() != ChangeType.ADD) {
      return false;
    }

    Microservice oldMicroservice = newMicroserviceMap.get(microserviceName);
    Microservice newMicroservice = newMicroserviceMap.get(microserviceName);
    int oldADS = MicroserviceMetricsService.calculateADS(oldMicroservice);
    int newADS = MicroserviceMetricsService.calculateADS(newMicroservice);

    // If our ADS (# of links) went down or remains the same
    // TODO I didn't understand the left half of this condition at first, I think we can implement
    // this better
    if (oldADS >= newADS || MicroserviceMetricsService.THRESHOLD > newADS) {
      return false;
    }

    // Otherwise if our ADS went up, and it's now above the threshold, we will blame all new valid
    // add's
    callChange.setImpact(RestCallImpact.HIGH_COUPLING);
    return true;
  }

  // Cycle stuff

  /**
   * Generates a microservice graph used for cyclic analysis
   * uses newMicroserviceMap and returns it
   *
   * @return the microservice graph
   */
  public MicroserviceGraph buildMicroserviceGraph() {
    Set<Node> nodes = newMicroserviceMap.keySet().stream().map(name -> new Node(name)).collect(Collectors.toUnmodifiableSet());
    Set<Link> links = new HashSet<>();

    // Create the links
    for (Microservice microservice : newMicroserviceMap.values()) {
      for (JService service : microservice.getServices()) {
        for (RestCall restCall : service.getRestCalls()) {
          if (Objects.nonNull(restCall.getDestFile()) && !restCall.getDestFile().isEmpty()) {
            Microservice destMicroservice = oldMicroserviceMap.get(restCall.getDestMsId());
            if (Objects.nonNull(destMicroservice)) {
              Optional<Link> existingLink = links.stream().filter(l -> l.getSource().equals(microservice.getId()) && l.getTarget().equals(destMicroservice.getId())).findFirst();
              // Keep track of at least the function name and where the ms is and file
              Request request = new Request(microservice.getId(), service.getClassPath(), restCall);

              if(existingLink.isPresent()) {
                links.remove(existingLink.get());
                existingLink.get().getRequests().add(request);
                links.add(existingLink.get());
              } else {
                List<Request> rList = new ArrayList<>();
                rList.add(request);
                links.add(new Link(microservice.getId(), destMicroservice.getId(), rList));
              }
            }
          }
        }
      }
    }

    return new MicroserviceGraph(nodes, links);
  }

  /**
   * Function to calculate all cyclic dependencies each node (microservice)
   * is a member of.
   *
   * @return map of microservice list pairs
   */
  public Map<String, List<Set<Node>>> getCyclicDependencies() {
    MicroserviceGraph graph = buildMicroserviceGraph();

    // Find the strongly connected components
    Graph<Set<Node>> sccs = graph.findSCCs();
    // Reduce SCCs to only those containing multiple nodes
    List<Set<Node>> sccList = sccs.nodes().stream().filter(scc -> scc.size() > 1).toList();
    Map<String, List<Set<Node>>> cyclicDeps = new HashMap<>();
    // Iterate over the strongly connected components and add cyclic dependency
    // tags to applicable nodes
    for (Set<Node> scc :  sccList) {
      scc.forEach(node -> graph.getNodes().stream().filter(node2 ->
                  node2.filterByName(node.getNodeName())).findFirst().ifPresent(
                  n -> {
                    cyclicDeps.putIfAbsent(n.getNodeName(), new ArrayList<>());
                    cyclicDeps.computeIfPresent(n.getNodeName(), (key, existingList) -> {
                      existingList.add(scc);
                      return existingList;
                    });
                  }
              )
      );
    }

    return cyclicDeps;

  }

}
