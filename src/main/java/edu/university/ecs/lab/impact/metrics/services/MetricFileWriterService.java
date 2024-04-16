package edu.university.ecs.lab.impact.metrics.services;

import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.JsonConvertUtils;
import edu.university.ecs.lab.impact.models.ClassMetrics;
import edu.university.ecs.lab.impact.models.SystemMetrics;
import edu.university.ecs.lab.impact.models.change.CallChange;
import edu.university.ecs.lab.impact.models.change.EndpointChange;
import edu.university.ecs.lab.impact.models.change.Link;
import edu.university.ecs.lab.impact.models.change.Metric;
import edu.university.ecs.lab.impact.models.dependency.ApiDependency;
import edu.university.ecs.lab.impact.models.dependency.EntityDependency;

import javax.json.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static edu.university.ecs.lab.common.writers.MsJsonWriter.writeJsonToFile;

/**
 * Logic for writing metrics to files
 */
public class MetricFileWriterService {
    private final MetricsService manager;

    MetricFileWriterService(MetricsService manager) {
        this.manager = manager;
    }

    /** @apiNote Austin - I would like the structure of
     * <ul>
     *     <li>Overall Class metrics {Controller, DTO, Service, etc.}</li>
     *     <li>Overall Dependency metrics {Entity, API, etc.}</li>
     *     <li>Then metrics for each microservice {...}</li>
     * </ul>
     * */
    public void writeMetricsToFile(String fileName, SystemMetrics systemMetrics) throws IOException {
        writePlaceholdersToFile(fileName);
    }

    private static JsonArray convertCallChangeToJsonArray(List<CallChange> callChangeList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (CallChange callChange : callChangeList) {
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

            jsonObjectBuilder.add("oldRestCall", JsonConvertUtils.buildRestCall(callChange.getOldCall()));
            jsonObjectBuilder.add("newRestCall", JsonConvertUtils.buildRestCall(callChange.getNewCall()));
            jsonObjectBuilder.add(
                    "oldLink",
                    Objects.isNull(callChange.getOldLink())
                            ? ""
                            : callChange.getOldLink().getMsSource()
                            + " -> "
                            + callChange.getOldLink().getMsDestination());
            jsonObjectBuilder.add(
                    "newLink",
                    Objects.isNull(callChange.getNewLink())
                            ? ""
                            : callChange.getNewLink().getMsSource()
                            + " -> "
                            + callChange.getNewLink().getMsDestination());
            jsonObjectBuilder.add("action", callChange.getAction());
            jsonObjectBuilder.add("impact", callChange.getImpact());

            arrayBuilder.add(jsonObjectBuilder.build());
        }
        return arrayBuilder.build();
    }

    private static JsonArray convertEndpointChangeToJsonArray(List<EndpointChange> callChangeList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (EndpointChange endpointChange : callChangeList) {
            JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

            jsonObjectBuilder.add(
                    "oldEndpoint", JsonConvertUtils.buildMethod(endpointChange.getOldEndpoint()));
            jsonObjectBuilder.add(
                    "newEndpoint", JsonConvertUtils.buildMethod(endpointChange.getNewEndpoint()));
            jsonObjectBuilder.add(
                    "oldLinks", convertLinkListToJsonArray(endpointChange.getOldLinkList()));
            jsonObjectBuilder.add(
                    "newLinks", convertLinkListToJsonArray(endpointChange.getNewLinkList()));
            jsonObjectBuilder.add("action", endpointChange.getAction());
            jsonObjectBuilder.add("impact", "");

            arrayBuilder.add(jsonObjectBuilder.build());
        }
        return arrayBuilder.build();
    }

    private static JsonArray convertLinkListToJsonArray(List<Link> linkList) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Link link : linkList) {
            arrayBuilder.add(link.getMsSource() + " -> " + link.getMsDestination());
        }

        return arrayBuilder.build();
    }

    private void writePlaceholdersToFile(String fileName) throws IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

        List<Metric> metricList = manager.getPlaceholders();
        for (Metric metric : metricList) {
            objectBuilder.add("MicroserviceName", metric.getMicroserviceName());
            objectBuilder.add("Filepath", metric.getFilePath());
            objectBuilder.add("ClassRole", metric.getClassRole().toString());
            if (metric.getClassRole().equals(ClassRole.SERVICE)) {
                objectBuilder.add("CallChanges", convertCallChangeToJsonArray(metric.getCallChangeList()));
            } else if (metric.getClassRole().equals(ClassRole.CONTROLLER)) {
                objectBuilder.add(
                        "EndpointChanges", convertEndpointChangeToJsonArray(metric.getEndpointChangeList()));
            }

            arrayBuilder.add(objectBuilder.build());
        }

        writeJsonToFile(arrayBuilder.build(), fileName);
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
            if (apiDependency.getStatus() != null) {
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

}
