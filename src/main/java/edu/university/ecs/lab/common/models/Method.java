package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.List;

import static edu.university.ecs.lab.common.utils.ObjectToJsonUtils.listToJsonArray;

/** Represents a method declaration in Java. */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Method implements JsonSerializable {
  protected String methodName;

  // Protection Not Yet Implemented
  // protected String protection;

  @SerializedName("parameter")
  protected String parameterList;

  protected String returnType;

  /** Method definition level annotations **/
  protected List<Annotation> annotations;

  @Override
  public JsonObject toJsonObject() {
    return createBuilder().build();
  }

  protected JsonObjectBuilder createBuilder() {
    JsonObjectBuilder methodObjectBuilder = Json.createObjectBuilder();

    methodObjectBuilder.add("methodName", methodName);
    methodObjectBuilder.add("parameter", parameterList);
    methodObjectBuilder.add("returnType", returnType);
    methodObjectBuilder.add("annotations", listToJsonArray(annotations));

    return methodObjectBuilder;
  }
}
