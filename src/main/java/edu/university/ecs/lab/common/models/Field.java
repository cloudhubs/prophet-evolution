package edu.university.ecs.lab.common.models;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/** Represents a field attribute in a Java class or in our case a JClass. */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Field implements JsonSerializable {
  @SerializedName("variableType")
  private String fieldType;

  @SerializedName("variableName")
  private String fieldName;

  public Field(VariableDeclarator variable) {
    setFieldName(variable.getNameAsString());
    setFieldType(variable.getTypeAsString());
  }

  @Override
  public JsonObject toJsonObject() {
    JsonObjectBuilder builder = Json.createObjectBuilder();

    builder.add("variableType", getFieldType());
    builder.add("variableName", getFieldName());
    return builder.build();
  }
}
