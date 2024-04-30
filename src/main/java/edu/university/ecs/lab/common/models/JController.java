package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

import static edu.university.ecs.lab.common.utils.ObjectToJsonUtils.listToJsonArray;

@Getter
@Setter
public class JController extends JClass implements JsonSerializable {

  @SerializedName("restEndpoints")
  private List<Endpoint> endpoints;

  public JController(JClass jClass) {
    super(
            jClass.getClassName(),
            jClass.getClassPath(),
            jClass.getPackageName(),
            jClass.getClassRole(),
            jClass.getMethods(),
            jClass.getFields(),
            jClass.getMethodCalls(),
            jClass.getMsId()
    );

    this.endpoints = new ArrayList<>();
  }

  @Override
  public JsonObject toJsonObject() {
    JsonObjectBuilder builder = super.createBuilder();

    builder.add("restEndpoints", listToJsonArray(endpoints));

    return builder.build();
  }
}
