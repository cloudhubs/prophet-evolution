package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class MsSystem {
  private String systemName;
  private String version;

  @SerializedName("microservices")
  private List<Microservice> msList;

  public Map<String, Microservice> getServiceMap() {
    Map<String, Microservice> msMap = new LinkedHashMap<>();

    for (Microservice ms : msList) {
      msMap.put(ms.getId(), ms);
    }

    return msMap;
  }
}
