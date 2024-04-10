package edu.university.ecs.lab.common.utils;

import edu.university.ecs.lab.common.models.*;

import javax.json.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/** Utility class for converting objects to  JSON. */
public class JsonConvertUtils {
  /** Private constructor to prevent instantiation. */
  private JsonConvertUtils() {}

  /**
   * Construct a JSON object representing the given ms system name, version, and microservice data
   * map.
   *
   * @param systemName the name of the system
   * @param version the version of the system
   * @param msDataMap the map of microservices to their data models
   * @return the constructed JSON object
   */
  public static JsonObject buildSystem(
      String systemName, String version, Map<String, Microservice> msDataMap) {
    JsonObjectBuilder parentBuilder = Json.createObjectBuilder();

    parentBuilder.add("systemName", systemName);
    parentBuilder.add("version", version);

    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

    for (Map.Entry<String, Microservice> microservice : msDataMap.entrySet()) {
      JsonObjectBuilder msObjectBuilder = Json.createObjectBuilder();
      String msName = microservice.getKey();

      if (microservice.getKey().contains(File.separator)) {
        msName =
            microservice.getKey().substring(microservice.getKey().lastIndexOf(File.separator) + 1);
      }

      msObjectBuilder.add("id", microservice.getValue().getId().replaceAll("\\\\", "/"));
      msObjectBuilder.add("msName", msName);
      msObjectBuilder.add("commitId", microservice.getValue().getCommit());

      msObjectBuilder.add(
          "controllers", buildRestControllers(msName, microservice.getValue().getControllers()));

      msObjectBuilder.add("services", buildRestServices(microservice.getValue().getServices()));
      msObjectBuilder.add("dtos", buildJavaClasses(microservice.getValue().getDtos()));
      msObjectBuilder.add(
          "repositories", buildJavaClasses(microservice.getValue().getRepositories()));
      msObjectBuilder.add("entities", buildJavaClasses(microservice.getValue().getEntities()));

      jsonArrayBuilder.add(msObjectBuilder.build());
    }

    parentBuilder.add("microservices", jsonArrayBuilder.build());
    return parentBuilder.build();
  }

  /**
   * Construct a list of JController objects as a JsonArray
   *
   * @param msName microservice system name
   * @param controllers list of JController objects to be converted
   * @return Converted JsonArray of JController objects
   */
  public static JsonArray buildRestControllers(String msName, List<JController> controllers) {
    JsonArrayBuilder controllerArrayBuilder = Json.createArrayBuilder();

    for (JController controller : controllers) {
      controllerArrayBuilder.add(buildRestController(msName, controller));
    }

    return controllerArrayBuilder.build();
  }

  /**
   * Convert a single JController to a JsonObject
   *
   * @param msName microservice system name
   * @param controller JController to be converted
   * @return Converted JsonObject of JController object
   */
  public static JsonObject buildRestController(String msName, JController controller) {
    JsonObjectBuilder controllerBuilder = Json.createObjectBuilder(buildJavaClass(controller));

    JsonArrayBuilder endpointArrayBuilder = Json.createArrayBuilder();

    // Get "endpoint" methods in controller
    for (Endpoint endpoint : controller.getEndpoints()) {
      endpointArrayBuilder.add(buildEndpoint(msName, endpoint));
    }

    controllerBuilder.add("restEndpoints", endpointArrayBuilder.build());


    return controllerBuilder.build();
  }

  /**
   * Constructs a list of JService objects as a JsonArray
   *
   * @param services list of JService objects to be converted
   * @return Converted JsonArray of JService objects
   */
  public static JsonArray buildRestServices(List<JService> services) {
    JsonArrayBuilder serviceArrayBuilder = Json.createArrayBuilder();

    for (JService service : services) {
      serviceArrayBuilder.add(buildRestService(service));
    }

    return serviceArrayBuilder.build();
  }

  /**
   * Convert a single JService to a JsonObject
   *
   * @param service JService to be converted
   * @return Converted JsonObject of JService object
   */
  public static JsonObject buildRestService(JService service) {
    JsonObjectBuilder serviceBuilder = Json.createObjectBuilder(buildJavaClass(service));
    if (service.getClassName() == null) {
      System.out.println("here");
    }

    serviceBuilder.add("restCalls", buildRestCalls(service.getRestCalls()));

    return serviceBuilder.build();
  }

  /**
   * Constructs a list of JClass objects as a JsonArray
   *
   * @param jClasses list of JClass objects to be converted
   * @return Converted JsonArray of JClass objects
   */
  public static JsonArray buildJavaClasses(List<JClass> jClasses) {
    JsonArrayBuilder jClassArrayBuilder = Json.createArrayBuilder();

    for (JClass jClass : jClasses) {
      jClassArrayBuilder.add(buildJavaClass(jClass));
    }

    return jClassArrayBuilder.build();
  }

  /**
   * Convert a single JClass to a JsonObject
   *
   * @param jClass JClass to be converted
   * @return Converted JsonObject of JClass object
   */
  public static JsonObject buildJavaClass(JClass jClass) {
    JsonObjectBuilder jClassBuilder = Json.createObjectBuilder();

    jClassBuilder.add("className", jClass.getClassName());
    jClassBuilder.add("classPath", jClass.getClassPath().replaceAll("\\\\", "/"));
    jClassBuilder.add("methods", buildMethodArray(jClass.getMethods()));
    jClassBuilder.add("variables", buildFieldArray(jClass.getFields()));
    jClassBuilder.add("methodCalls", buildMethodCallArray(jClass.getMethodCalls()));

    return jClassBuilder.build();
  }

  /**
   * Constructs a list of RestCall objects as a JsonArray
   *
   * @param restCalls list of RestCall objects to be converted
   * @return Converted JsonArray of RestCall objects
   */
  public static JsonArray buildRestCalls(List<RestCall> restCalls) {
    JsonArrayBuilder restCallArrayBuilder = Json.createArrayBuilder();

    // Get "restCall" methodCalls in service
    for (RestCall restCall : restCalls) {
      JsonObjectBuilder restCallBuilder = Json.createObjectBuilder();

      // TODO source this issue
      if (restCall.getDestFile() == null) {
        restCall.setDestFile("");
      }

      restCallBuilder.add("api", restCall.getApi());
      restCallBuilder.add("source-file", restCall.getSourceFile().replaceAll("\\\\", "/"));
      restCallBuilder.add("call-dest", restCall.getDestFile().replaceAll("\\\\", "/"));
      restCallBuilder.add("call-method", restCall.getMethodName() + "()");
      restCallBuilder.add("httpMethod", restCall.getHttpMethod());

      restCallArrayBuilder.add(restCallBuilder.build());
    }

    return restCallArrayBuilder.build();
  }

  /**
   * Constructs a list of Method objects as a JsonArray
   *
   * @param methodList list of Method objects to be converted
   * @return Converted JsonArray of Method objects
   */
  public static JsonArray buildMethodArray(List<Method> methodList) {
    // TODO find cause of this
    if (methodList == null) {
      return JsonObject.EMPTY_JSON_ARRAY;
    }
    JsonArrayBuilder methodArrayBuilder = Json.createArrayBuilder();

    for (Method method : methodList) {
      JsonObjectBuilder methodObjectBuilder = Json.createObjectBuilder();

      methodObjectBuilder.add("methodName", method.getMethodName());
      methodObjectBuilder.add("parameter", method.getParameterList());
      methodObjectBuilder.add("returnType", method.getReturnType());

      methodArrayBuilder.add(methodObjectBuilder.build());
    }

    return methodArrayBuilder.build();
  }

  /**
   * Constructs a list of Method objects as a JsonArray
   *
   * @param methodCallList list of Method objects to be converted
   * @return Converted JsonArray of Method objects
   */
  public static JsonArray buildMethodCallArray(List<MethodCall> methodCallList) {
    // TODO find cause of this
    if (methodCallList == null) {
      return JsonObject.EMPTY_JSON_ARRAY;
    }
    JsonArrayBuilder methodArrayBuilder = Json.createArrayBuilder();

    for (MethodCall methodCall : methodCallList) {
      JsonObjectBuilder methodObjectBuilder = Json.createObjectBuilder();

      methodObjectBuilder.add("methodName", methodCall.getMethodName());
      methodObjectBuilder.add("parentMethod", methodCall.getParentMethod());
      methodObjectBuilder.add("calledFieldName", methodCall.getCalledFieldName());

      methodArrayBuilder.add(methodObjectBuilder.build());
    }

    return methodArrayBuilder.build();
  }

  /**
   * Constructs a list of Field objects as a JsonArray
   *
   * @param fieldList list of Field objects to be converted
   * @return Converted JsonArray of Field objects
   */
  public static JsonArray buildFieldArray(List<Field> fieldList) {
    // TODO find cause of this
    if (fieldList == null) {
      return JsonObject.EMPTY_JSON_ARRAY;
    }

    JsonArrayBuilder variableArrayBuilder = Json.createArrayBuilder();
    for (Field field : fieldList) {
      JsonObjectBuilder variableObjectBuilder = Json.createObjectBuilder();

      variableObjectBuilder.add("variableName", field.getFieldName());
      variableObjectBuilder.add("variableType", field.getFieldType());

      variableArrayBuilder.add(variableObjectBuilder);
    }

    return variableArrayBuilder.build();
  }

  /**
   * Construct a JSON object representing the given ms system name, version, and microservice data
   * map.
   *
   * @param systemName the name of the system
   * @param version the version of the system
   * @param clonesMap the map of microservices to their clones
   * @return the constructed JSON object
   */
  //  public static JsonObject constructJsonClonesSystem(
  //      String systemName, String version, Map<String, List<CodeClone>> clonesMap) {
  //    JsonObjectBuilder parentBuilder = Json.createObjectBuilder();
  //
  //    parentBuilder.add("systemName", systemName);
  //    parentBuilder.add("version", version);
  //
  //    JsonArrayBuilder microserviceArrayBuilder = Json.createArrayBuilder();
  //
  //    for (Map.Entry<String, List<CodeClone>> microservice : clonesMap.entrySet()) {
  //
  //      JsonObjectBuilder microserviceBuilder = Json.createObjectBuilder();
  //      String msName = microservice.getKey();
  //      if (microservice.getKey().contains(File.separator)) {
  //        msName =
  //            microservice.getKey().substring(microservice.getKey().lastIndexOf(File.separator) +
  // 1);
  //      }
  //      microserviceBuilder.add("msName", msName);
  //
  //      JsonArrayBuilder cloneArrayBuilder = Json.createArrayBuilder();
  //      boolean hasClones = false;
  //
  //      for (CodeClone clone : microservice.getValue()) {
  //        JsonObjectBuilder cloneBuilder = Json.createObjectBuilder();
  //
  //        cloneBuilder.add("global-similarity", clone.getGlobalSimilarity());
  //        cloneBuilder.add("controller-similarity", clone.getSimilarityController());
  //        cloneBuilder.add("service-similarity", clone.getSimilarityService());
  //        cloneBuilder.add("repository-similarity", clone.getSimilarityRepository());
  //        cloneBuilder.add("restCalls-similarity", clone.getSimilarityRestCalls());
  //        cloneBuilder.add("flowA", constructFlowJson(clone.getFlowA()));
  //        cloneBuilder.add("flowB", constructFlowJson(clone.getFlowB()));
  //
  //        cloneArrayBuilder.add(cloneBuilder.build());
  //        hasClones = true;
  //      }
  //      microserviceBuilder.add("clones", cloneArrayBuilder.build());
  //
  //      if (hasClones) {
  //        microserviceArrayBuilder.add(microserviceBuilder.build());
  //      }
  //    }
  //
  //    parentBuilder.add("services", microserviceArrayBuilder.build());
  //
  //    return parentBuilder.build();
  //  }

  //  private static JsonObject constructFlowJson(Flow flow) {
  //    JsonObjectBuilder flowBuilder = Json.createObjectBuilder();
  //
  //    // add Controller to Json
  //    if (Objects.nonNull(flow.getController())) {
  //      flowBuilder.add("microservice", flow.getController().getId().getProject());
  //      flowBuilder.add("controller", flow.getController().getClassName());
  //      flowBuilder.add("controller-method", flow.getControllerMethod().getMethodName());
  //    }
  //
  //    // Add service to Json
  //    if (Objects.nonNull(flow.getService())) {
  //      flowBuilder.add("service", flow.getService().getClassName());
  //      flowBuilder.add("service-method", flow.getServiceMethod().getMethodName());
  //    }
  //
  //    // Add repository to Json
  //    if (Objects.nonNull(flow.getRepository())) {
  //      flowBuilder.add("repository", flow.getRepository().getClassName());
  //      flowBuilder.add("repository-method", flow.getRepositoryMethod().getMethodName());
  //    }
  //
  //    // Add Rest calls to Json
  //    if (Objects.nonNull(flow.getRestCalls())) {
  //      JsonArrayBuilder restCallArrayBuilder = Json.createArrayBuilder();
  //
  //      for (edu.university.ecs.lab.semantics.models.RestCall restCall : flow.getRestCalls()) {
  //        JsonObjectBuilder restCallBuilder = Json.createObjectBuilder();
  //        restCallBuilder.add("api-endpoint", restCall.getApiEndpoint());
  //        restCallBuilder.add("http-method", restCall.getHttpMethod());
  //        restCallBuilder.add("return-type", restCall.getReturnType());
  //
  //        restCallArrayBuilder.add(restCallBuilder.build());
  //      }
  //
  //      flowBuilder.add("rest-calls", restCallArrayBuilder.build());
  //    }
  //
  //    return flowBuilder.build();
  //  }

  /**
   * Constructs a String endpointId from an Endpoint object and name of microservice
   *
   * @param msName the name of the microservice system
   * @param endpoint endpoint object used in construction
   * @return a unique Id representing this endpoint
   */
  private static String buildEndpointId(String msName, Endpoint endpoint) {
    return endpoint.getHttpMethod()
        + ":"
        + msName
        + "."
        + endpoint.getMethodName()
        + "#"
        + Math.abs(endpoint.getParameterList().hashCode());
  }

  /**
   * Convert a single Endpoint to a JsonObject
   *
   * @param endpoint Endpoint to be converted
   * @return Converted JsonObject of Endpoint object
   */
  private static JsonObject buildEndpoint(String msName, Endpoint endpoint) {
    JsonObjectBuilder endpointBuilder = Json.createObjectBuilder();

    String id = buildEndpointId(msName, endpoint);

    endpointBuilder.add("id", id);
    endpointBuilder.add("api", endpoint.getUrl());
    endpointBuilder.add("type", endpoint.getDecorator());
    endpointBuilder.add("httpMethod", endpoint.getHttpMethod());
    endpointBuilder.add("methodName", endpoint.getMethodName());
    endpointBuilder.add("parameter", endpoint.getParameterList());
    endpointBuilder.add("returnType", endpoint.getReturnType());

    return endpointBuilder.build();
  }
}
