package edu.university.ecs.lab.impact.metrics;

import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.IRParserUtils;
import edu.university.ecs.lab.common.utils.JsonConvertUtils;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import edu.university.ecs.lab.impact.models.ClassMetrics;
import edu.university.ecs.lab.impact.models.DependencyMetrics;
import edu.university.ecs.lab.impact.models.SystemMetrics;
import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.change.Link;
import edu.university.ecs.lab.impact.models.change.Placeholder;
import edu.university.ecs.lab.impact.models.dependency.ApiDependency;
import edu.university.ecs.lab.impact.models.dependency.EntityDependency;
import edu.university.ecs.lab.impact.models.enums.Status;
import netscape.javascript.JSObject;
import org.checkerframework.checker.units.qual.A;
import org.glassfish.json.JsonUtil;

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
//        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
//
//        jsonObjectBuilder.add("EntityDependencyMetrics", convertEntityListToJsonArray(systemMetrics.getDependencyMetrics().getEntityDependencyList()));
//        jsonObjectBuilder.add("ApiDependencyMetrics", convertApiListToJsonArray(systemMetrics.getDependencyMetrics().getApiDependencyList()));
//        jsonObjectBuilder.add("ControllerMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(0)));
//        jsonObjectBuilder.add("ServiceMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(1)));
//        jsonObjectBuilder.add("RepoMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(2)));
//        jsonObjectBuilder.add("DtoMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(3)));
//        jsonObjectBuilder.add("EntityMetrics", convertClassMetricToJsonObject(systemMetrics.getClassMetrics().get(4)));
//
//
//
//
//        writeJsonToFile(jsonObjectBuilder.build(), fileName);
        writePlaceholdersToFile(fileName);
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
        List<EntityDependency> entityDependencyList = compareEntityDependencies();
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

    private List<EntityDependency> compareEntityDependencies() {
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

    private static JsonArray convertCallChangeToJsonArray(List<CallChange> callChangeList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (CallChange callChange : callChangeList) {
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

            jsonObjectBuilder.add("oldRestCall", JsonConvertUtils.buildRestCall(callChange.getOldCall()));
            jsonObjectBuilder.add("newRestCall", JsonConvertUtils.buildRestCall(callChange.getNewCall()));
            jsonObjectBuilder.add("oldLink", callChange.getOldLink().getMsSource() + " -> " + callChange.getOldLink().getMsDestination());
            jsonObjectBuilder.add("newLink", callChange.getNewLink().getMsSource() + " -> " + callChange.getNewLink().getMsDestination());
            jsonObjectBuilder.add("impact", "");

            arrayBuilder.add(jsonObjectBuilder.build());

        }
        return arrayBuilder.build();
    }

    private void writePlaceholdersToFile(String fileName) throws IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        List<Placeholder> placeholderList = getPlaceholders();
        for(Placeholder placeholder : placeholderList) {
            objectBuilder.add("MicroserviceName", placeholder.getMicroserviceName());
            objectBuilder.add("Filepath", placeholder.getFilePath());
            objectBuilder.add("ClassRole", placeholder.getClassRole().toString());
            objectBuilder.add("CallChanges", convertCallChangeToJsonArray(placeholder.getCallChangeList()));

            arrayBuilder.add(objectBuilder.build());
        }

        writeJsonToFile(arrayBuilder.build(), fileName);

    }

    public List<Placeholder> getPlaceholders() {
        List<Placeholder> placeholderList = new ArrayList<>();
        Placeholder placeholder;

        if(Objects.nonNull(systemChange.getServices()) && !systemChange.getServices().isEmpty()) {
            for(Delta delta : systemChange.getServices()) {
                placeholder = new Placeholder();
                placeholder.setFilePath(delta.getLocalPath());
                placeholder.setCallChangeList(getAllRestCallChanges(delta));
                placeholder.setClassRole(ClassRole.SERVICE);
                placeholder.setChangeType(delta.getChangeType());
                placeholder.setMicroserviceName(delta.getMsName());
                placeholderList.add(placeholder);
                // RestCall Changes

            }
        }

        return placeholderList;
    }

    public List<CallChange> getAllRestCallChanges(Delta delta) {
        List<CallChange> callChanges = new ArrayList<>();
        JService oldService = null;


        switch (delta.getChangeType()) {
            case ADD:
                callChanges.addAll(compareRestCalls(null, delta.getSChange()));
                break;
            case MODIFY:
                oldService = oldMicroserviceMap.get(delta.getMsName()).getServices().stream().filter(s -> s.getClassPath().equals(delta.getLocalPath())).findFirst().orElse(null);
                callChanges.addAll(compareRestCalls(oldService, delta.getSChange()));
                break;
            case DELETE:
                oldService = oldMicroserviceMap.get(delta.getMsName()).getServices().stream().filter(s -> s.getClassPath().equals(delta.getLocalPath())).findFirst().orElse(null);
                callChanges.addAll(compareRestCalls(oldService, null));
                break;
        }

        return callChanges;
    }

    /**
     * Compare all restCalls in oldService to newService
     * no concept of 'modified' calls there are only oldLinks
     * and newLinks
     *
     * @param oldService
     * @param newService
     * @return
     */
    private List<CallChange> compareRestCalls(JService oldService, JService newService) {
        List<CallChange> callChanges = new ArrayList<>();

        // If it is an added class
        if(oldService == null) {
            for(RestCall newCall : newService.getRestCalls()) {
                callChanges.add(buildCallChange(null, newCall));
            }

            return callChanges;
        }

        // If it is a deleted class
        if(newService == null) {
            for(RestCall oldCall : oldService.getRestCalls()) {
                callChanges.add(buildCallChange(oldCall, null));
            }

            return callChanges;
        }

        List<RestCall> oldRestCalls = oldService.getRestCalls();
        List<RestCall> newRestCalls = newService.getRestCalls();

        for(RestCall oldCall : oldRestCalls) {
            if(!newRestCalls.remove(oldCall)) {
                // If no call removed, it isn't present (removed)
                callChanges.add(buildCallChange(oldCall, null));
            }
        }

        for(RestCall newCall : newRestCalls) {
            if(!oldRestCalls.remove(newCall)) {
                // If no call was removed, it isn't present (added)
                callChanges.add(buildCallChange(null, newCall));
            }
        }

        return callChanges;
    }

    private CallChange buildCallChange(RestCall oldCall, RestCall newCall) {
        CallChange callChange = new CallChange();
        callChange.setOldCall(oldCall);
        callChange.setNewCall(newCall);
        callChange.setOldLink(new Link("old", "new"));
        callChange.setNewLink(new Link("old", "new"));


        return callChange;
    }

}
