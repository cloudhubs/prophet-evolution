package edu.university.ecs.lab.common.utils;

import edu.university.ecs.lab.common.models.*;

import javax.json.*;
import java.util.List;
import java.util.Map;

/** Utility class for converting objects to JSON. */
public class ObjectToJsonUtils {
  /** Private constructor to prevent instantiation. */
  private ObjectToJsonUtils() {}

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

    // TODO version number is static rn, check IRExtractionService
    parentBuilder.add("version", version);

    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

    for (Microservice microservice : msDataMap.values()) {
      JsonObjectBuilder msObjectBuilder = Json.createObjectBuilder();

      msObjectBuilder.add("id", microservice.getId());
      msObjectBuilder.add("commitId", microservice.getCommit());

      msObjectBuilder.add(
          "controllers", buildRestControllers(microservice.getId(), microservice.getControllers()));

      msObjectBuilder.add("services", buildRestServices(microservice.getServices()));
      msObjectBuilder.add("dtos", buildJavaClasses(microservice.getDtos()));
      msObjectBuilder.add(
          "repositories", buildJavaClasses(microservice.getRepositories()));
      msObjectBuilder.add("entities", buildJavaClasses(microservice.getEntities()));

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
    jClassBuilder.add("id", jClass.getId());
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
      restCallArrayBuilder.add(buildRestCall(restCall));
    }

    return restCallArrayBuilder.build();
  }

  /**
   * @param restCall RestCall object to be converted
   * @return Converted JsonObject of RestCall object
   */
  public static JsonObject buildRestCall(RestCall restCall) {
    // Get "restCall" methodCalls in service
    JsonObjectBuilder restCallBuilder = Json.createObjectBuilder();
    if (restCall == null) {
      return JsonValue.EMPTY_JSON_OBJECT;
    }

    // TODO source this issue
    if (restCall.getDestFile() == null) {
      restCall.setDestFile("");
    }

    restCallBuilder.add("api", restCall.getApi());
    restCallBuilder.add("source-file", restCall.getSourceFile().replaceAll("\\\\", "/"));
    restCallBuilder.add("call-dest", restCall.getDestFile().replaceAll("\\\\", "/"));
    restCallBuilder.add("call-method", restCall.getMethodName() + "()");
    restCallBuilder.add("httpMethod", restCall.getHttpMethod());

    return restCallBuilder.build();
  }

  /**
   * Constructs a list of Method objects as a JsonArray
   *
   * @param methodList list of Method objects to be converted
   * @return Converted JsonArray of Method objects
   */
  public static JsonArray buildMethodArray(List<Method> methodList) {

    if (methodList == null) {
      return JsonObject.EMPTY_JSON_ARRAY;
    }

    JsonArrayBuilder methodArrayBuilder = Json.createArrayBuilder();

    for (Method method : methodList) {
      methodArrayBuilder.add(buildMethod(method));
    }

    return methodArrayBuilder.build();
  }

  /**
   * Constructs a list of Method objects as a JsonArray
   *
   * @param method method to write to JSON
   * @return Converted {@link JsonObject} of Method
   */
  public static JsonObject buildMethod(Method method) {
    // TODO find cause of this
    if (method == null) {
      return JsonObject.EMPTY_JSON_OBJECT;
    }

    JsonObjectBuilder methodObjectBuilder = Json.createObjectBuilder();

    methodObjectBuilder.add("methodName", method.getMethodName());
    methodObjectBuilder.add("parameter", method.getParameterList());
    methodObjectBuilder.add("returnType", method.getReturnType());

    return methodObjectBuilder.build();
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
