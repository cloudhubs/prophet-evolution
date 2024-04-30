package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.university.ecs.lab.common.utils.ObjectToJsonUtils.listToJsonArray;

/**
 * Represents an extension of a method declaration. An endpoint exists at the controller level and
 * signifies an open mapping that can be the target of a rest call.
 */
@Data
@AllArgsConstructor
@ToString
public class Endpoint extends Method implements JsonSerializable {
  @SerializedName("api")
  private String url;

  @SerializedName("type")
  private String decorator;

  private String httpMethod;

  private String msId;

  @SerializedName("src-calls")
  private List<RestCall.EndpointCall> srcCalls;

  // Not Yet Implemented
  // private String mapping;
  // private String mappingPath;

  public Endpoint(Method method, String url, String decorator, String httpMethod, String msId) {
    super(method.getMethodName(), method.getParameterList(), method.getReturnType());
    setMsId(msId);
    setUrl(url);
    setDecorator(decorator);
    setHttpMethod(httpMethod);
    setSrcCalls(new ArrayList<>());
  }

  @Override
    public JsonObject toJsonObject() {
      JsonObjectBuilder endpointBuilder = super.createBuilder();

      endpointBuilder.add("id", getId());
      endpointBuilder.add("api", url);
      endpointBuilder.add("type", decorator);
      endpointBuilder.add("httpMethod", httpMethod);
      endpointBuilder.add("msId", msId);
      endpointBuilder.add("src-calls", listToJsonArray(srcCalls));

      return endpointBuilder.build();
    }

  /**
   * Constructs a String endpointId from an Endpoint object and name of microservice.
   *
   * @return a unique Id representing this endpoint
   */
  public String getId() {
    return "[" + httpMethod + "]" + msId + ":" + url;
  }

    /**
     * Check if the given RestCall matches this endpoint. Does not use restCall destMsId or destFile as these may not be set yet.
     *
     * @param restCall the call to check
     * @return true if the call matches this endpoint, false otherwise
     */
  public boolean matchCall(RestCall restCall) {

    if (!this.httpMethod.equals(restCall.getHttpMethod())) {
      return false;
    }


    boolean isUrlMatch = this.url.equals(restCall.getDestEndpoint());

    // TODO TESTING, this is a hack to handle endpoints with params
    if (!isUrlMatch) {
      String urlWithoutParams = this.url.split("/\\{")[0];
      if (!Objects.equals(urlWithoutParams, this.url)) {
        isUrlMatch = urlWithoutParams.equals(restCall.getDestEndpoint());
      }
    }

    return isUrlMatch;
  }

  /**
   * Compare this endpoint to another (changed) endpoint to determine if they are the same.
   * @return true if the endpoints are the same, false otherwise
   */
  public boolean isSameEndpoint(Endpoint other) {
    return Objects.equals(httpMethod, other.getHttpMethod())
            && Objects.equals(url, other.getUrl());
  }

  /**
   * Add a call to the list of calls that use this endpoint.
   * @param restCall the call to add
   */
  public void addCall(RestCall restCall, JService service) {
    srcCalls.add(new RestCall.EndpointCall(restCall, service));
  }
}
