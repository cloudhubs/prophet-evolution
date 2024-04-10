package edu.university.ecs.lab.impact.metrics;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.IRParserUtils;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.ClassMetrics;
import edu.university.ecs.lab.impact.models.DependencyMetrics;
import edu.university.ecs.lab.impact.models.SystemMetrics;
import edu.university.ecs.lab.impact.models.dependency.ApiDependency;
import edu.university.ecs.lab.impact.models.dependency.EntityDependency;
import edu.university.ecs.lab.impact.models.enums.Status;
import netscape.javascript.JSObject;
import org.checkerframework.checker.units.qual.A;

import javax.json.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static edu.university.ecs.lab.common.utils.FlowUtils.buildFlows;
import static edu.university.ecs.lab.common.writers.MsJsonWriter.writeJsonToFile;

public class MetricsManager {

    private Map<String, Microservice> oldMicroserviceMap;
    private Map<String, Microservice> newMicroserviceMap;

    private SystemChange systemChange;

    public MetricsManager(String oldIrPath, String newIrPath, String deltaPath) throws IOException {
        oldMicroserviceMap = IRParserUtils.parseIRSystem(oldIrPath).getServiceMap();
        newMicroserviceMap = IRParserUtils.parseIRSystem(newIrPath).getServiceMap();
        systemChange = IRParserUtils.parseSystemChange(deltaPath);
    }

    private void writeMetricsToFile(String fileName, SystemMetrics systemMetrics) throws IOException {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        jsonObjectBuilder.add("EntityDependencyMetrics", convertEntityListToJsonArray(systemMetrics.getDependencyMetrics().getEntityDependencyList()));
        jsonObjectBuilder.add("ApiDependencyMetrics", convertApiListToJsonArray(systemMetrics.getDependencyMetrics().getApiDependencyList()));
        jsonObjectBuilder.add("ControllerMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(0)));
        jsonObjectBuilder.add("ServiceMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(1)));
        jsonObjectBuilder.add("RepoMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(2)));
        jsonObjectBuilder.add("DtoMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(3)));
        jsonObjectBuilder.add("EntityMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(4)));




        writeJsonToFile(jsonObjectBuilder.build(), fileName);

    }

    private static JsonObject convertClassMetricToJsonObject(ClassMetrics classMetrics) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

        jsonObjectBuilder.add("numAdded", classMetrics.getAddedClassCount());
        jsonObjectBuilder.add("numRemoved", classMetrics.getRemovedClassCount());
        jsonObjectBuilder.add("numModified", classMetrics.getModifiedClassCount());
//        jsonObjectBuilder.add("numEndpoint", classMetrics.getEndpointCount().toString());
//        jsonObjectBuilder.add("numRestCall", classMetrics.getApiCallCount().toString());



        return jsonObjectBuilder.build();
    }

    private static JsonArray convertEntityListToJsonArray(List<EntityDependency> jsonObjectList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (EntityDependency entityDependency : jsonObjectList) {
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

            jsonObjectBuilder.add("entity-class", entityDependency.getEntityClass());
            jsonObjectBuilder.add("controller-class", entityDependency.getEndpointClass());
            jsonObjectBuilder.add("microservice", entityDependency.getService());
            jsonObjectBuilder.add("status", entityDependency.getStatus().toString());

            arrayBuilder.add(jsonObjectBuilder.build());
        }
        return arrayBuilder.build();
    }

    private static JsonArray convertApiListToJsonArray(List<ApiDependency> jsonObjectList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (ApiDependency apiDependency : jsonObjectList) {
            if(apiDependency.getStatus() != null) {
                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

                jsonObjectBuilder.add("source-class", apiDependency.getSourceClass());
                jsonObjectBuilder.add("dest-class", apiDependency.getDestClass());
                jsonObjectBuilder.add("source-service", apiDependency.getSourceService());
                jsonObjectBuilder.add("dest-service", apiDependency.getDestService());
                jsonObjectBuilder.add("status", apiDependency.getStatus().toString());

                arrayBuilder.add(jsonObjectBuilder.build());
            }
        }
        return arrayBuilder.build();
    }


    public SystemMetrics generateSystemMetrics() throws IOException {
        SystemMetrics systemMetrics = new SystemMetrics();

        systemMetrics.setDependencyMetrics(generateDependencyMetrics(systemChange));
        systemMetrics.setClassMetrics(generateAllClassMetrics());

        writeMetricsToFile("Metrics.txt", systemMetrics);

        return systemMetrics;
    }

    private List<ClassMetrics> generateAllClassMetrics() {
        List<ClassMetrics> classMetricsList = new ArrayList<>();

        classMetricsList.add(generateClassMetrics(ClassRole.CONTROLLER, systemChange.getControllers()));
        classMetricsList.add(generateClassMetrics(ClassRole.SERVICE, systemChange.getServices()));
        classMetricsList.add(generateClassMetrics(ClassRole.REPOSITORY, systemChange.getRepositories()));
        classMetricsList.add(generateClassMetrics(ClassRole.DTO, systemChange.getDtos()));
        classMetricsList.add(generateClassMetrics(ClassRole.ENTITY, systemChange.getEntities()));

        return classMetricsList;
    }

    private ClassMetrics generateClassMetrics(ClassRole classRole, List<Delta> changeList) {
        ClassMetrics classMetrics = new ClassMetrics();
        classMetrics.setClassRole(classRole);

        for(Delta delta : changeList) {

            switch (delta.getChangeType()) {
                case ADD:
                    classMetrics.incrementAddedClassCount();
                    break;
                case MODIFY:
                    classMetrics.incrementModifiedClassCount();
                    break;
                case DELETE:
                    classMetrics.incrementRemovedClassCount();
                    break;
            }

        }
        return classMetrics;
    }

    private DependencyMetrics generateDependencyMetrics(SystemChange systemChange) {
        List<EntityDependency> entityDependencyList = compareEntityDependencies(systemChange);
        List<ApiDependency> apiDependencyList = compareApiDependencies();

        return new DependencyMetrics(apiDependencyList, entityDependencyList);
    }


//    private void addAllIndirectApiDependencies(String msName, Status status, List<ApiDependency> apiDependencies, JController jController) {
//        // If there is no flow to the serviceLayer
//        if(jController.getMethodCalls().isEmpty()) {
//            return;
//        }
//
//        Microservice microservice = microserviceMap.get(msName);
//
//        if(Objects.isNull(microservice)) {
//            return;
//        }
//
//        List<Flow> flows = buildFlows(microservice);
//
//        for(Flow flow : flows) {
//            if(flow.getController().getClassName().equals(jController)) {
//
//            }
//        }
//
//        for(Endpoint endpoint : jController.getEndpoints()) {
//            if(endpoint.get) {
//                apiDependencies.add(
//                        new ApiDependency(status,
//                                IRParserUtils.getClassNameFromLocalPath(restCall.getSourceFile()),
//                                IRParserUtils.getClassNameFromLocalPath(restCall.getDestFile()),
//                                IRParserUtils.getClassNameFromLocalPath(restCall.getSourceFile()),
//                                IRParserUtils.getClassNameFromLocalPath(restCall.getDestFile()),
//                                false)
//                );
//            }
//        }
//    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private List<EntityDependency> compareEntityDependencies(SystemChange systemChange) {
        List<EntityDependency> finalDependencies = new ArrayList<>();
        List<EntityDependency> oldDependencies = locateAllEntityDependencies(oldMicroserviceMap);
        List<EntityDependency> newDependencies = locateAllEntityDependencies(newMicroserviceMap);

        for(EntityDependency oldDependency : oldDependencies) {
            if(newDependencies.contains(oldDependency)) {
                Microservice oldMicroservice = oldMicroserviceMap.values().stream().filter(ms -> ms.getId().equals(oldDependency.getService())).findFirst().orElse(null);
                Microservice newMicroservice = newMicroserviceMap.values().stream().filter(ms -> ms.getId().equals(oldDependency.getService())).findFirst().orElse(null);

                JClass oldEntity = oldMicroservice.getEntities().stream().filter(oldE -> oldE.getClassName().equals(oldDependency.getEntityClass())).findFirst().orElse(null);
                JClass newEntity = newMicroservice.getEntities().stream().filter(oldE -> oldE.getClassName().equals(oldDependency.getEntityClass())).findFirst().orElse(null);

                // If the relevant entity was changed or unmodified
                if(Objects.nonNull(oldEntity) && Objects.nonNull(newEntity) && Objects.equals(oldEntity, newEntity)) {
                    oldDependency.setStatus(Status.UNMODIFIED);
                } else {
                    oldDependency.setStatus(Status.MODIFIED);
                }

                finalDependencies.add(oldDependency);
            } else {
                oldDependency.setStatus(Status.DELETED);
                finalDependencies.add(oldDependency);
            }
        }

        for(EntityDependency newDependency : newDependencies) {
            if(!oldDependencies.contains(newDependency)) {
                newDependency.setStatus(Status.CREATED);
                finalDependencies.add(newDependency);
            }
        }

        return finalDependencies;
    }

    private List<ApiDependency> compareApiDependencies() {
        List<ApiDependency> finalDependencies = new ArrayList<>();
        List<ApiDependency> oldDependencies = locateAllApiDependencies(oldMicroserviceMap);
        List<ApiDependency> newDependencies = locateAllApiDependencies(newMicroserviceMap);

        for(ApiDependency oldDependency : oldDependencies) {
            if(newDependencies.contains(oldDependency)) {
                // If the relevant entity was changed or unmodified
                oldDependency.setStatus(Status.UNMODIFIED);
                finalDependencies.add(oldDependency);
            } else {
                oldDependency.setStatus(Status.DELETED);
                finalDependencies.add(oldDependency);
            }
        }

        for(ApiDependency newDependency : newDependencies) {
            if(!oldDependencies.contains(newDependency)) {
                newDependency.setStatus(Status.CREATED);
                finalDependencies.add(newDependency);
            }
        }

        return finalDependencies;
    }



    private List<ApiDependency> locateAllApiDependencies(Map<String, Microservice> microserviceMap) {
        List<ApiDependency> apiDependencyList = new ArrayList<>();

        for(Map.Entry<String, Microservice> microserviceEntry : microserviceMap.entrySet()) {
            apiDependencyList.addAll(getDirectApiDependencies(microserviceEntry.getValue()));
//            apiDependencyList.addAll(getIndirectApiDependencies(microserviceEntry.getValue()));
        }

        return apiDependencyList;
    }

    private List<ApiDependency> getDirectApiDependencies(Microservice microservice) {
        List<ApiDependency> apiDependencies = new ArrayList<>();

        for(JService jService : microservice.getServices()) {
            for (RestCall restCall : jService.getRestCalls()) {
                if (!restCall.getDestFile().isEmpty()) {
                    apiDependencies.add(
                            new ApiDependency(null,
                                    IRParserUtils.getClassNameFromLocalPath(restCall.getSourceFile()),
                                    IRParserUtils.getClassNameFromLocalPath(restCall.getDestFile()),
                                    IRParserUtils.getClassNameFromLocalPath(restCall.getSourceFile()),
                                    IRParserUtils.getClassNameFromLocalPath(restCall.getDestFile()),
                                    true)
                    );
                }
            }
        }

        return apiDependencies;
    }

    private List<EntityDependency> locateAllEntityDependencies(Map<String, Microservice> microserviceMap) {
        List<EntityDependency> entityDependencyList = new ArrayList<>();

        for(Map.Entry<String, Microservice> microserviceEntry : microserviceMap.entrySet()) {
            entityDependencyList.addAll(getEntityDependencies(microserviceEntry.getValue()));
//            apiDependencyList.addAll(getIndirectApiDependencies(microserviceEntry.getValue()));
        }

        return entityDependencyList;
    }

    private List<EntityDependency> getEntityDependencies(Microservice microservice) {
        List<EntityDependency> apiDependencyList = new ArrayList<>();
        EntityDependency entityDependency;

//        // If no entities or dto's are modified then entity dependencies are not affected
//        if(checkForEntityModification(systemChange)) {
//            return;
//        }

        List<Flow> flows = buildFlows(microservice);

        for(JClass entity : microservice.getEntities()) {
            for(Flow flow : flows) {
                if(flow.getControllerMethod().getParameterList().contains(entity.getClassName())) {
                    entityDependency = new EntityDependency(null,
                        microservice.getId(),
                        entity.getClassName(),
                        flow.getController().getClassName()
                    );
                    apiDependencyList.add(entityDependency);
                }
            }

        }



        return apiDependencyList;
    }


    /**
     *
     *
     * @param flows
     * @param delta
     */
    private static void identifyImpact(List<Flow> flows, Delta delta) {
        for(Flow flow : flows) {
            String className = delta.getLocalPath().substring(delta.getLocalPath().lastIndexOf("\\") + 1, delta.getLocalPath().lastIndexOf(".java"));
            // If a parameter in the controller method (endpoint) contains the classname of deleted entity
            if(flow.getControllerMethod().getParameterList().contains(className)) {
                System.out.println("Here we are deleting a reliant object affecting endpoint url: " + ((Endpoint) flow.getControllerMethod()).getUrl());
            }
        }
    }

    /**
     * Validate first that the old object exists in IR
     * and second that the new object differs
     *
     * @param model ms system this change occurred in
     * @param delta delta change
     *
     */
//    private static boolean validateObjectChange(Microservice model, String className) throws RuntimeException {
//        Optional<JClass> oldClass = Stream.concat(model.getEntities().stream(), model.getDtos().stream()).filter(jClass -> jClass.getClassPath().contains(className)).findFirst();
//
//        if(oldClass.isEmpty()) {
//            throw new RuntimeException("Shouldn't be possible");
//        }
//
//        if(Objects.equals(oldClass.get(), delta.getChange())) {
//            return false;
//        }
//
//        return true;
//    }

    /**
     * Checks if relevant changes are made to either dtos or entities
     *
     * @param systemChange object representing changes to a system
     * @return if relevant changes are present
     */
    private static boolean checkForEntityModification(SystemChange systemChange) {
        return (systemChange.getEntities().isEmpty() && systemChange.getDtos().isEmpty());
    }

}
