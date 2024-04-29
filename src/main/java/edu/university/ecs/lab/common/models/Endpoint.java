package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.university.ecs.lab.common.utils.ObjectToJsonUtils.stringListToJsonArray;

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

  @SerializedName("src-call-ids")
  private List<String> srcCallIds;

  // Not Yet Implemented
  // private String mapping;
  // private String mappingPath;

  public Endpoint(Method method, String url, String decorator, String httpMethod, String msId) {
    super(method.getMethodName(), method.getParameterList(), method.getReturnType());
    setMsId(msId);
    setUrl(url);
    setDecorator(decorator);
    setHttpMethod(httpMethod);
    setSrcCallIds(new ArrayList<>());
  }

  @Override
    public JsonObject toJsonObject() {
      JsonObjectBuilder endpointBuilder = super.createBuilder();

      endpointBuilder.add("id", getId());
      endpointBuilder.add("api", url);
      endpointBuilder.add("type", decorator);
      endpointBuilder.add("httpMethod", httpMethod);
      endpointBuilder.add("msId", msId);
      endpointBuilder.add("src-call-ids", stringListToJsonArray(srcCallIds));

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

  public boolean matchCall(RestCall restCall) {

    boolean isUrlMatch = this.url.equals(restCall.getDestEndpoint());

    // TODO TESTING, this is a hack to handle endpoints with params
    if (!isUrlMatch) {
      String urlWithoutParams = this.url.split("/\\{")[0];
      if (urlWithoutParams.length() != this.url.length()) {
        isUrlMatch = urlWithoutParams.equals(restCall.getDestEndpoint());
      }
    }

    return (isUrlMatch)
            && this.httpMethod.equals(restCall.getHttpMethod()) &&
            this.msId.equals(restCall.getDestMsId());
  }

  /**
   * Compare this endpoint to another (changed) endpoint to determine if they are the same.
   * @return true if the endpoints are the same, false otherwise
   */
  public boolean isSameEndpoint(Endpoint other) {
    return Objects.equals(httpMethod, other.getHttpMethod())
            && Objects.equals(url, other.getUrl())
            && Objects.equals(decorator, other.getDecorator());
  }

  /**
   * Add a call to the list of calls that use this endpoint.
   * @param restCall the call to add
   */
  public void addCall(RestCall restCall) {
    srcCallIds.add(restCall.getId());
  }
}
