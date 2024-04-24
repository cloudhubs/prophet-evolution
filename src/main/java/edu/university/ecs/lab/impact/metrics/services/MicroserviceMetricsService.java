package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.MicroserviceMetrics;
import edu.university.ecs.lab.impact.models.change.Link;

import java.util.*;

public class MicroserviceMetricsService {
    Map<String, Microservice> microserviceMap;
    SystemChange systemChange;

    public MicroserviceMetricsService(Map<String, Microservice> microserviceMap, SystemChange systemChange) {
        this.microserviceMap = microserviceMap;
        this.systemChange = systemChange;
    }

    public List<MicroserviceMetrics> getMicroserviceMetrics() {
        List<MicroserviceMetrics> microserviceMetricsList = new ArrayList<MicroserviceMetrics>();



        return microserviceMetricsList;
    }

    /*
        https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

            Service Interface Data Cohesion (SIDC1): this metric

        quantifies the cohesion of a service based on the cohesive-
        ness of the operations exposed in its interface, which means

        operations sharing the same type of input parameter [16].

        SIDC2 evolves from SIDC1 by considering both in/out pa-
        rameter types. Formal definition:

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
    private double calculateSIDC2Score(Microservice microservice) {
        Map<String, Integer> paramTypes = new HashMap<>();
        Map<String, Integer> returnTypes = new HashMap<>();
        int commonParams = 0;
        int commonReturns = 0;
        int totalEndpoints = 0;

        for(JController controller : microservice.getControllers()) {
            outer:
            {
                // Check deltaChange file for controller
                for (Delta delta : systemChange.getControllers()) {
                    if (delta.getLocalPath().equals(controller.getClassPath())) {
                        if (delta.getChangeType() == ChangeType.DELETE) {
                            break outer;
                        } else {
                            JController deltaController = delta.getCChange();
                            // Calculate duplicates
                            for(Endpoint endpoint: deltaController.getEndpoints()) {
                                totalEndpoints++;
                                String[] params = endpoint.getParameterList().split(",");
                                for(String param : params) {
                                    String[] paramParts = param.split(" ");
                                    if(paramParts.length == 2) {
                                        // If we try to add it but it was already there
                                        if(paramTypes.containsKey(paramParts[0])) {
                                            paramTypes.merge(paramParts[0], 1, Integer::sum);
                                            commonParams += (paramTypes.get(paramParts[0]) - 1) * 2;
                                        } else {
                                            paramTypes.put(paramParts[0], 1);
                                        }
                                    }

                                }

                                if(returnTypes.containsKey(endpoint.getReturnType())) {
                                    returnTypes.merge(endpoint.getReturnType(), 1, Integer::sum);
                                    commonReturns += (returnTypes.get(endpoint.getReturnType()) - 1) * 2;
                                } else {
                                    returnTypes.put(endpoint.getReturnType(), 1);
                                }
                            }

                            break outer;
                        }
                    }
                }

                for(Endpoint endpoint: controller.getEndpoints()) {
                    totalEndpoints++;
                    String[] params = endpoint.getParameterList().split(",");
                    for(String param : params) {
                        String[] paramParts = param.split(" ");
                        if(paramParts.length == 2) {
                            // If we try to add it but it was already there
                            if(paramTypes.containsKey(paramParts[0])) {
                                paramTypes.merge(paramParts[0], 1, Integer::sum);
                                commonParams += (paramTypes.get(paramParts[0]) - 1) * 2;
                            } else {
                                paramTypes.put(paramParts[0], 1);
                            }
                        }
                    }

                    if(returnTypes.containsKey(endpoint.getReturnType())) {
                        returnTypes.merge(endpoint.getReturnType(), 1, Integer::sum);
                        commonReturns += (returnTypes.get(endpoint.getReturnType()) - 1) * 2;
                    } else {
                        returnTypes.put(endpoint.getReturnType(), 1);
                    }
                }

            }
        }

        return (double) (commonParams + commonReturns) / (totalEndpoints * 2);
    }

    public boolean aboveThreshold(Microservice microservice) {
        return (calculateADS(microservice) > 5);
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

    /*
        https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

        Service Interface Usage Cohesion (SIUC): this metric
        quantifies the client usage patterns of service operations
        when a client invokes a service. Formal definition:
        SIUC(s) = Invoked(clients, SOp(sis ))/
        (|clients| ∗ |SOp(sis )|), where
        – clients is the set of all clients of service s.
        – Invoked(clients, SOp(sis )) returns the number of used
        operations per client.
     */
    private double calculateSIUCScore(Microservice microservice) {
        Set<String> clients = new HashSet<>();
        int usedOperations = 0;
        long totalOperations = microserviceMap.values().stream().flatMap(ms -> ms.getServices().stream()).flatMap(jService -> jService.getRestCalls().stream()).count();

        for(JController controller : microservice.getControllers()) {
            outer:
            {
                for (Delta delta : systemChange.getControllers()) {

                    if (controller.getClassPath().equals(delta.getLocalPath())) {
                        // If the controller was deleted
                        if (delta.getChangeType() == ChangeType.DELETE) {
                            break outer;
                        }
                        JController deltaController = delta.getCChange();

                        // Loop through endpoints
                        for (Endpoint endpoint : deltaController.getEndpoints()) {

                            // Look for links from "clients"
                            for (Microservice ms : microserviceMap.values()) {
                                outer2:
                                {
                                    for (JService service : ms.getServices()) {
                                        for (Delta delta2 : systemChange.getServices()) {
                                            if (service.getClassPath().equals(delta2.getLocalPath())) {
                                                // If the service was deleted
                                                if (delta2.getChangeType() == ChangeType.DELETE) {
                                                    break outer2;
                                                }
                                                JService deltaService = delta.getSChange();

                                                for (RestCall restCall : deltaService.getRestCalls()) {
                                                    if (restCall.getApi().equals(endpoint.getUrl())) {
                                                        usedOperations++;
                                                        clients.add(ms.getId());
                                                    }
                                                }

                                            }
                                        }

                                        for (RestCall restCall : service.getRestCalls()) {
                                            if (restCall.getApi().equals(endpoint.getUrl())) {
                                                usedOperations++;
                                                clients.add(ms.getId());
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

            }

        }

        return (double) usedOperations / clients.size() * totalOperations;
    }

    /*
        https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

        Absolute Dependence of the Service (ADS): number of services on which the S1
        service depends. In other words, ADS is the number of services that S1 calls for its
        operation to be complete. The higher the ADS, the more this service depends on other
        services, i.e., it is more vulnerable to the side effects of failures in the services invoked.
     */
    public int calculateADS(Microservice microservice) {
        Set<Link> links = new HashSet<>();
        for(JService service : microservice.getServices()) {
            outer:
            {
                for (Delta delta : systemChange.getServices()) {
                    if (service.getClassPath().equals(delta.getLocalPath())) {
                        JService deltaService = delta.getSChange();
                        if (delta.getChangeType() != ChangeType.DELETE) {
                            for (RestCall restCall : deltaService.getRestCalls()) {
                                RestCall updatedRestCall = updateRestCallDest(restCall);

                                links.add(new Link(updatedRestCall));

                            }
                        }
                        break outer;
                    }
                }

                for (RestCall restCall : service.getRestCalls()) {
                    RestCall updatedRestCall = updateRestCallDest(restCall);

                    links.add(new Link(updatedRestCall));

                }
            }
        }

        return links.size();
    }


}
