package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Represents the overarching structure of a microservice system. It is composed of classes which
 * hold all information in that class.
 */
@Data
@AllArgsConstructor
public class Microservice {
  @SerializedName("id")
  private String id;

  @SerializedName("commitId")
  private String commit;

  @SerializedName("msPath")
  private String msPath;

  /** List of classes */
  private List<JController> controllers;
  private List<JService> services;
  private List<JClass> dtos;
  private List<JClass> repositories;
  private List<JClass> entities;

  /** Default constructor, init lists as empty */
  public Microservice() {}

  public int getModelSize() {
    return controllers.size()
        + services.size()
        + dtos.size()
        + repositories.size()
        + entities.size();
  }
}
