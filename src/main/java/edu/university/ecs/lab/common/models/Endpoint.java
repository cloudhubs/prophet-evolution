package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

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

  // Not Yet Implemented
  // private String mapping;
  // private String mappingPath;

  // TODO this is dangerous as it leaves all new fields null
  public Endpoint(String mdId, Method method) {
    super(method.getMethodName(), method.getParameterList(), method.getReturnType());
    setMsId(mdId);
  }

  @Override
    public JsonObject toJsonObject() {
      JsonObjectBuilder endpointBuilder = super.createBuilder();

      endpointBuilder.add("id", getId());
      endpointBuilder.add("api", url);
      endpointBuilder.add("type", decorator);
      endpointBuilder.add("httpMethod", httpMethod);
      endpointBuilder.add("msId", msId);

      return endpointBuilder.build();
    }

  /**
   * Constructs a String endpointId from an Endpoint object and name of microservice
   *
   * @return a unique Id representing this endpoint
   */
  private String getId() {
    return httpMethod
            + ":"
            + msId
            + "."
            + methodName
            + "#"
            + Math.abs(parameterList.hashCode());
  }
}
