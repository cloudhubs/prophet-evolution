package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/** Represents a method declaration in Java. */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Method implements JsonSerializable {
  /** Name of the method */
  protected String methodName;

  // Protection Not Yet Implemented
  // protected String protection;

  /** List of parameters in the method as a string like: [String userId, String money] */
  @SerializedName("parameter")
  protected String parameterList;

  /** Java return type of the method */
  protected String returnType;

  @Override
  public JsonObject toJsonObject() {
    return createBuilder().build();
  }

  protected JsonObjectBuilder createBuilder() {
    JsonObjectBuilder methodObjectBuilder = Json.createObjectBuilder();

    methodObjectBuilder.add("methodName", methodName);
    methodObjectBuilder.add("parameter", parameterList);
    methodObjectBuilder.add("returnType", returnType);

    return methodObjectBuilder;
  }
}
