package edu.university.ecs.lab.common.models;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.Objects;

/** Represents a field attribute in a Java class or in our case a JClass. */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Field {
  @SerializedName("variableType")
  private String fieldType;

  @SerializedName("variableName")
  private String fieldName;

  public Field(VariableDeclarator variable) {
    setFieldName(variable.getNameAsString());
    setFieldType(variable.getTypeAsString());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Field field = (Field) o;
    return Objects.equals(fieldType, field.fieldType) && Objects.equals(fieldName, field.fieldName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldType, fieldName);
  }
}
