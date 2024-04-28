package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static edu.university.ecs.lab.common.utils.ObjectToJsonUtils.listToJsonArray;

/**
 * Represents the overarching structure of a microservice system. It is composed of classes which
 * hold all information in that class.
 */
@Getter
public class Microservice implements JsonSerializable {
  /** The name of the service (ex: "ts-assurance-service") */
  @SerializedName("id")
  private String id;

  /** The commit id of the service as cloned */
  @SerializedName("commitId")
  private String commit;

  /** Controller classes belonging to the microservice. */
  private List<JController> controllers;
  /** Service classes to the microservice. */
  private List<JService> services;
  /** DTO classes belonging to the microservice. */
  private List<JClass> dtos;
  /** Repository classes belonging to the microservice. */
  private List<JClass> repositories;
  /** Entity classes belonging to the microservice. */
  private List<JClass> entities;

  public void setId(String id) {
    Objects.requireNonNull(id, "id cannot be null");
    this.id = id.replaceAll("\\\\", "/");
    propagateId(this.controllers);
    propagateId(this.services);
    propagateId(this.dtos);
    propagateId(this.repositories);
    propagateId(this.entities);
  }

  public void setCommit(String commit) {
    Objects.requireNonNull(commit, "commit cannot be null");
    this.commit = commit;
  }

  public void setControllers(List<JController> controllers) {
    Objects.requireNonNull(controllers, "controllers cannot be null");
    this.controllers = controllers;
    propagateId(this.controllers);
  }

  public void setServices(List<JService> services) {
    Objects.requireNonNull(services, "services cannot be null");
    this.services = services;
    propagateId(this.services);
  }

  public void setDtos(List<JClass> dtos) {
    Objects.requireNonNull(dtos, "dtos cannot be null");
    this.dtos = dtos;
    propagateId(this.dtos);
  }

  public void setRepositories(List<JClass> repositories) {
    Objects.requireNonNull(repositories, "repositories cannot be null");
    this.repositories = repositories;
    propagateId(this.repositories);
  }

  public void setEntities(List<JClass> entities) {
    Objects.requireNonNull(entities, "entities cannot be null");
    this.entities = entities;
    propagateId(this.entities);
  }

  private <T extends JClass> void propagateId(List<T> classes) {
    Objects.requireNonNull(this.id);
    if (classes == null) {
      return;
    }
    classes.forEach(c -> c.setMsId(id));
  }

  /**
   * Constructor for the microservice object
   *
   * @param id the name of the service
   * @param commit the commit id of the service
   * @param controllers the controllers in the service
   * @param services the services in the service
   * @param dtos the dtos in the service
   * @param repositories the repositories in the service
   * @param entities the entities in the service
   */
  public Microservice(String id,
                      String commit,
                      List<JController> controllers,
                      List<JService> services,
                      List<JClass> dtos,
                      List<JClass> repositories,
                      List<JClass> entities) {
    this.setCommit(commit);
    this.setId(id);
    this.setControllers(controllers);
    this.setServices(services);
    this.setDtos(dtos);
    this.setRepositories(repositories);
    this.setEntities(entities);
  }

  /**
   * Constructor for the microservice object with all lists as empty
   *
   * @param id the name of the service
   * @param commit the commit # of the service
   */
  public Microservice(String id, String commit) {
    this.setId(id);
    this.setCommit(commit);
    this.setControllers(new ArrayList<>());
    this.setServices(new ArrayList<>());
    this.setRepositories(new ArrayList<>());
    this.setDtos(new ArrayList<>());
    this.setEntities(new ArrayList<>());
  }

  @Override
  public JsonObject toJsonObject() {
    JsonObjectBuilder builder = Json.createObjectBuilder();

    builder.add("id", id);
    builder.add("commitId", commit);
    builder.add("controllers", listToJsonArray(controllers));
    builder.add("services", listToJsonArray(services));
    builder.add("dtos", listToJsonArray(services));
    builder.add("repositories", listToJsonArray(repositories));
    builder.add("entities", listToJsonArray(entities));

    return builder.build();
  }
}
