package edu.university.ecs.lab.common.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/** Represents a method call in Java. */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MethodCall implements JsonSerializable {
  protected String methodName;
  // TODO Rename this? Represents if the called method object e.g. test.test()
  protected String calledFieldName;
  protected String parentMethod;


  @Override
  public JsonObject toJsonObject() {
    return createBuilder().build();
  }

  protected JsonObjectBuilder createBuilder() {
    JsonObjectBuilder methodObjectBuilder = Json.createObjectBuilder();

    methodObjectBuilder.add("methodName", this.methodName);
    methodObjectBuilder.add("parentMethod", this.parentMethod);
    methodObjectBuilder.add("calledFieldName", this.calledFieldName);

    return methodObjectBuilder;
  }
}
