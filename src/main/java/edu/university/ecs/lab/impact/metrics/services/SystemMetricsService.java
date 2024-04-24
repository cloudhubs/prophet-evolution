package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.change.Link;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SystemMetricsService {
    Map<String, Microservice> microserviceMap;
    SystemChange systemChange;

    public SystemMetricsService(Map<String, Microservice> microserviceMap, SystemChange systemChange) {
        this.microserviceMap = microserviceMap;
        this.systemChange = systemChange;
    }

    /*
        https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

        Average number of directly connected services (ADCS): the average of ADS metric
        of all services.
     */
    public double calculateADCS() {
        MicroserviceMetricsService microserviceMetricsService = new MicroserviceMetricsService(microserviceMap, systemChange);
        int totalADS = 0;
        for(Microservice microservice : microserviceMap.values()) {
            totalADS += microserviceMetricsService.calculateADS(microservice);
        }

        return (double) totalADS / microserviceMap.size();
    }

    /*
        https://drive.google.com/drive/folders/1nEknAyMAsw-aUze0_ot9_lER2yNV-HOx

        Service Coupling Factor (SCF): measure of the density of a graph’s connectivity.
        SCF = SC/(N2 − N), where SC (service coupling) is a sum of all dependencies
        between services. That is, each service that can invoke operations from another
        service adds one more to this value. N is the total number of services. If we represent
        dependencies as a graph, SC is the sum of all edges and N2 − N represents the
        maximum oriented edges the graph can have.
     */
    public double calculateSCF() {
        Set<Link> links = new HashSet<Link>();

        for(Microservice microservice : microserviceMap.values()) {
            for (JService service : microservice.getServices()) {
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
        }

        return (double) links.size() / (microserviceMap.values().size() * microserviceMap.values().size()) - microserviceMap.values().size();
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
//
//    private boolean detectCyclicDependency() {
//        for (Microservice microservice1 : microserviceMap.values()) {
//            for (JService jService1 : microservice1.getServices()) {
//                outer1:
//                {
//                    for (Delta delta : systemChange.getServices()) {
//                        if (jService1.getClassPath().equals(delta.getLocalPath())) {
//                            if (delta.getChangeType() != ChangeType.DELETE) {
//                                break outer1;
//                            }
//                            JService deltaService1 = delta.getSChange();
//
//                            for(RestCall restCall1 : deltaService1.getRestCalls()) {
//                                ///////////////////
//
//                                for (Microservice microservice2 : microserviceMap.values()) {
//                                    if (!microservice2.getId().equals(microservice1.getId())) {
//                                        for (JController jController1 : microservice2.getControllers()) {
//                                            outer2:
//                                            {
//                                                for (Delta delta2 : systemChange.getControllers()) {
//                                                    if (jController1.getClassPath().equals(delta2.getLocalPath())) {
//                                                        if (delta2.getChangeType() != ChangeType.DELETE) {
//                                                            break outer1;
//                                                        }
//                                                        JController deltaController1 = delta.getCChange();
//
//                                                        for (Endpoint endpoint1 : deltaController1.getEndpoints()) {
//                                                            if(endpoint1.getUrl().equals(restCall1.getApi())) {
//                                                                for (JService jService2 : microservice2.getServices()) {
//
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//                                                }
//
//                                                for (Endpoint endpoint1 : jController1.getEndpoints()) {
//                                                    if(endpoint1.getUrl().equals(restCall1.getApi())) {
//                                                        for (JService jService2 : microservice2.getServices()) {
//
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//
//                                ///////////////////
//                            }
//                        }
//                    }
//
//                    for (RestCall restCall : jService1.getRestCalls()) {
//
//                    }
//                }
//            }
//        }
//    }
}
