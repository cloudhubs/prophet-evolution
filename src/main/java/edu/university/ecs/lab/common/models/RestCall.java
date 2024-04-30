package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.enums.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Represents an extension of a method call. A rest call exists at the service level and represents
 * a call to an endpoint mapping.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestCall extends MethodCall {
  /** The api url that is targeted in rest call */
  private String api = "";

  /**
   * The httpMethod of the api endpoint e.g. GET, POST, PUT see semantics.models.enums.httpMethod
   */
  private String httpMethod = "";

  /** Expected return type of the api call */
  //  private String returnType = "";

  private int responseTypeIndex = -1;

  @SerializedName("source-file")
  private String sourceFile = "";

  @SerializedName("call-dest")
  private String destFile = "";

  private static final RestCall[] restTemplates = {
    new RestCall("getForObject", HttpMethod.GET, 1),
    new RestCall("getForEntity", HttpMethod.GET, 1),
    new RestCall("postForObject", HttpMethod.POST, 2),
    new RestCall("postForEntity", HttpMethod.POST, 2),
    new RestCall("put", HttpMethod.PUT, 1),
    new RestCall("exchange", HttpMethod.GET, 3),
    new RestCall("delete", HttpMethod.DELETE, 0), // TODO: delete doesn't work
  };

  public RestCall(String methodName, HttpMethod httpMethod, int responseTypeIndex) {
    setMethodName(methodName);
    setHttpMethod(httpMethod.toString());
    setResponseTypeIndex(responseTypeIndex);
  }

  public RestCall(MethodCall methodCall) {
    methodName = methodCall.getMethodName();
    calledFieldName = methodCall.getCalledFieldName();
    parentMethod = methodCall.getParentMethod();
  }

  public static RestCall findByName(String methodName) {
    for (RestCall template : restTemplates) {
      if (template.getMethodName().equals(methodName)) {
        return template;
      }
    }
    return null;
  }

  public static RestCall findCallByName(String methodName) {
    switch (methodName) {
      case "getForObject":
        return new RestCall("getForObject", HttpMethod.GET, 1);
      case "getForEntity":
        return new RestCall("getForEntity", HttpMethod.GET, 1);
      case "postForObject":
        return new RestCall("postForObject", HttpMethod.POST, 2);
      case "postForEntity":
        return new RestCall("postForEntity", HttpMethod.POST, 2);
      case "put":
        return new RestCall("put", HttpMethod.PUT, 1);
      case "exchange":
        return new RestCall("exchange", HttpMethod.GET, 3);
      case "delete":
        new RestCall("delete", HttpMethod.DELETE, 0);
    }

    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    RestCall restCall = (RestCall) o;
    return responseTypeIndex == restCall.responseTypeIndex
        && Objects.equals(api, restCall.api)
        && Objects.equals(httpMethod, restCall.httpMethod)
        && Objects.equals(sourceFile, restCall.sourceFile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), api, httpMethod, responseTypeIndex, sourceFile, destFile);
  }
}
